//! imports
#include "spin1_api.h"
#include "common-typedefs.h"
#include <data_specification.h>
#include <simulation.h>
#include <debug.h>

//! How many mc packets are to be received per sdp packet
#define ITEMS_PER_DATA_PACKET 68

//! first sequence number to use and reset to
#define FIRST_SEQ_NUM 0

//! extra length adjustment for the sdp header
#define LENGTH_OF_SDP_HEADER 8

//! convert between words to bytes
#define WORD_TO_BYTE_MULTIPLIER 4

//! Code for ACK Packet
#define ACK_CODE 0

//! struct for a SDP message with pure data, no scp header
typedef struct sdp_msg_pure_data {	// SDP message (=292 bytes)
    struct sdp_msg *next;	// Next in free list
    uint16_t length;		// length
    uint16_t checksum;		// checksum (if used)

    // sdp_hdr_t
    uint8_t flags;	    	// SDP flag byte
    uint8_t tag;		// SDP IPtag
    uint8_t dest_port;		// SDP destination port/CPU
    uint8_t srce_port;		// SDP source port/CPU
    uint16_t dest_addr;		// SDP destination address
    uint16_t srce_addr;		// SDP source address

    // User data (272 bytes when no scp header)
    uint32_t data[ITEMS_PER_DATA_PACKET];

    uint32_t _PAD;		// Private padding
} sdp_msg_pure_data;

//! struct for the elements of the circular buffer
typedef struct buffer_elem {

	uint32_t data[ITEMS_PER_DATA_PACKET]; // Single packet

	uint size; // Size of the packet, necessary as it's not constant

} buffer_elem;

//! control value, which says how many timer ticks to run for before exiting
static uint32_t simulation_ticks = 0;
static uint32_t infinite_run = 0;
static uint32_t time = 0;

//! int as a bool to represent if this simulation should run forever
static uint32_t infinite_run;

//! the key that causes sequence number to be processed
static uint32_t new_sequence_key = 0;
static uint32_t first_data_key = 0;
static uint32_t end_flag_key = 0;

//! default seq num
static uint32_t seq_num = FIRST_SEQ_NUM;
static uint32_t max_seq_num = 0;

//! data holders for the sdp packet
static uint32_t data[ITEMS_PER_DATA_PACKET];
static uint32_t position_in_store = 0;

//! sdp message holder for transmissions
sdp_msg_pure_data my_msg;

//! Windowed protocol values
static uint32_t window_size = 0;
static uint32_t sliding_window = 0;
static uint32_t start_pos = 0;
static uint32_t end_pos = 0;
static uint8_t ack_received = 0;

//! Buffer containing all the packets of the window
static buffer_elem *buffer;

//! human readable definitions of each region in SDRAM
typedef enum regions_e {
    SYSTEM_REGION, CONFIG
} regions_e;

//! human readable definitions of the data in each region
typedef enum config_elements {
    NEW_SEQ_KEY, FIRST_DATA_KEY, END_FLAG_KEY, SLIDING_WINDOW, WINDOW_SIZE, TAG_ID
} config_elements;

//! values for the priority for each callback
typedef enum callback_priorities{
    MC_PACKET = 0, SDP = -1, DMA = 0
} callback_priorities;


void resume_callback() {
    time = UINT32_MAX;
}

void send_data(){
    //log_info("last element is %d", data[position_in_store - 1]);
    //log_info("first element is %d", data[0]);

    int pos;

    spin1_memcpy(&my_msg.data, data,
	    position_in_store * WORD_TO_BYTE_MULTIPLIER);
    my_msg.length =
	    LENGTH_OF_SDP_HEADER + (position_in_store * WORD_TO_BYTE_MULTIPLIER);
    //log_info("my length is %d with position %d", my_msg.length, position_in_store);

	//more efficient than the spin1_memcpy
	memcpy(&(buffer[data[0]+start_pos]->data), data, position_in_store * WORD_TO_BYTE_MULTIPLIER);
	buffer[data[0]+start_pos]->size = position_in_store * WORD_TO_BYTE_MULTIPLIER;

    if (seq_num > max_seq_num){
        log_error(
            "got a funky seq num in sending. max is %d, received %d",
            max_seq_num, seq_num);
    }

    while (!spin1_send_sdp_msg((sdp_msg_t *) &my_msg, 100)) {
	// Empty body
    }

    position_in_store = 1;
    seq_num += 1;
    data[0] = seq_num;
}

void re_send_window() {

	int i;

	if(end_pos > start_pos) {

		for(i = start_pos; i <= end_pos && !ack_received; i++) {

			memcpy(&my_msg.data, &(buffer[i]->data), buffer[i]->size);
			my_msg.length = LENGTH_OF_SDP_HEADER + buffer[i]->size;

			while(!spin1_send_sdp_msg((sdp_msg_t *) &my_msg, 100));
		}
	}
	else {

		for(i = start_pos; i < sliding_window && !ack_received; i++) {

			memcpy(&my_msg.data, &(buffer[i]->data), buffer[i]->size);
			my_msg.length = LENGTH_OF_SDP_HEADER + buffer[i]->size;

			while(!spin1_send_sdp_msg((sdp_msg_t *) &my_msg, 100));
		}

		for(i = 0 ; i <= end_pos && !ack_received; i++) {

			memcpy(&my_msg.data, &(buffer[i]->data), buffer[i]->size);
			my_msg.length = LENGTH_OF_SDP_HEADER + buffer[i]->size;

			while(!spin1_send_sdp_msg((sdp_msg_t *) &my_msg, 100));
		}
	}

	ack_received = 0;
}

void receive_ack(uint mailbox, uint port) {

	sdp_msg_pure_data *msg = (sdp_msg_pure_data *) mailbox;

	if(msg->data[0] == ACK_CODE) {

		start_pos = (start_pos + window_size) % sliding_window;
		end_pos = (end_pos + window_size) % sliding_window;
		ack_received = 1;
	}

	//Free the message
	spin1_msg_free(msg);
}

void receive_data(uint key, uint payload) {
    //log_info("packet!");
    if (key == new_sequence_key) {
        if (position_in_store != 1) {
            send_data();
        }
        //log_info("finding new seq num %d", payload);
        //log_info("position in store is %d", position_in_store);
        data[0] = payload;
        seq_num = payload;
        position_in_store = 1;

        if (payload > max_seq_num){
            log_error(
                "got a funky seq num. max is %d, received %d",
                max_seq_num, payload);
        }
    } else {

        //log_info(" payload = %d posiiton = %d", payload, position_in_store);
        data[position_in_store] = payload;
        position_in_store += 1;
        //log_info("payload is %d", payload);

        if (key == first_data_key) {
            //log_info("resetting seq and position");
            seq_num = FIRST_SEQ_NUM;
            data[0] = seq_num;
            position_in_store = 1;
            max_seq_num = payload;
        }

        if (key == end_flag_key){
            // set end flag bit in seq num
            data[0] = data[0] + (1 << 31);

            // adjust size as last payload not counted
            position_in_store = position_in_store - 1;

            //log_info("position = %d with seq num %d", position_in_store, seq_num);
            //log_info("last payload was %d", payload);
            send_data();
        } else if (position_in_store == ITEMS_PER_DATA_PACKET) {
            //log_info("position = %d with seq num %d", position_in_store, seq_num);
            //log_info("last payload was %d", payload);
            send_data();
        }

        // Window is terminated
        while(seq_num > end_pos) {

        	//No ACK received
        	re_send_window();
        }

        ack_received = 0;
    }
}

static bool initialize(uint32_t *timer_period) {

    log_info("Initialise: started\n");

    // Get the address this core's DTCM data starts at from SRAM
    address_t address = data_specification_get_data_address();

    // Read the header
    if (!data_specification_read_header(address)) {
        log_error("failed to read the data spec header");
        return false;
    }

    // Get the timing details and set up the simulation interface
    if (!simulation_initialise(
            data_specification_get_region(SYSTEM_REGION, address),
            APPLICATION_NAME_HASH, timer_period, &simulation_ticks,
            &infinite_run, SDP, DMA)) {
        return false;
    }

    address_t config_address = data_specification_get_region(CONFIG, address);
    new_sequence_key = config_address[NEW_SEQ_KEY];
    first_data_key = config_address[FIRST_DATA_KEY];
    end_flag_key = config_address[END_FLAG_KEY];

    sliding_window = config_address[SLIDING_WINDOW];
    window_size = config_address[WINDOW_SIZE];
    end_pos = sliding_window - 1;
    ack_received = 0;

    // Allocate the circular buffer containing all the packets of the current window
    if((buffer = (buffer_elem *) sark_alloc(sliding_window, sizeof(buffer_elem))) == NULL) {
		log_error("failed to allocate the packet buffer in DTCM");
    	return false;
    }

    my_msg.tag = config_address[TAG_ID];	// IPTag 1
    my_msg.dest_port = PORT_ETH;		// Ethernet
    my_msg.dest_addr = sv->eth_addr;		// Nearest Ethernet chip

    // fill in SDP source & flag fields
    my_msg.flags = 0x07;
    my_msg.srce_port = 3;
    my_msg.srce_addr = sv->p2p_addr;

    return true;
}

/****f*
 *
 * SUMMARY
 *  This function is called at application start-up.
 *  It is used to register event callbacks and begin the simulation.
 *
 * SYNOPSIS
 *  int c_main()
 *
 * SOURCE
 */
void c_main() {
    log_info("starting packet gatherer\n");

    // Load DTCM data
    uint32_t timer_period;

    // initialise the model
    if (!initialize(&timer_period)) {
        rt_error(RTE_SWERR);
    }

    spin1_callback_on(FRPL_PACKET_RECEIVED, receive_data, MC_PACKET);
    spin1_callback_on(SDP_PACKET_RX, receive_ack, SDP);

    // start execution
    log_info("Starting\n");

    // Start the time at "-1" so that the first tick will be 0
    time = UINT32_MAX;

    spin1_start(SYNC_NOWAIT);
}
