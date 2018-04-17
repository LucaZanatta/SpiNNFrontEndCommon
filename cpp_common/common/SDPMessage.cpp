#include "SDPMessage.h"

void SDPMessage::convert_to_byte_vector(std::vector<uint8_t> &buffer) const
{
    buffer.resize(length_in_bytes());
    header.write_header(buffer);
    memcpy(buffer.data() + header.length_bytes(), data, data_length);
}