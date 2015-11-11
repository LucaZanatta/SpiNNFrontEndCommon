from spinn_front_end_common.utilities.notification_protocol.\
    notification_protocol import NotificationProtocol

import logging


logger = logging.getLogger(__name__)


class FrontEndCommonNotificationProtocol(object):
    """
    FrontEndCommonNotificationProtocol: the notification protocol for
    exeternal device readings
    """

    def __call__(
            self, wait_for_read_confirmation,
            socket_addresses, database_interface):
        """

        :param wait_for_read_confirmation:
        :param socket_addresses:
        :param database_interface:
        :return:
        """

        # notification protocol
        self._notification_protocol = \
            NotificationProtocol(socket_addresses, wait_for_read_confirmation)
        self.send_read_notification(database_interface.database_file_path)

        return {"notification_interface": self}

    def wait_for_confirmation(self):
        """
        helper method which waits for devices to confirm they have read the
        databse via the notifiication protocol
        :return:
        """
        self._notification_protocol.wait_for_confirmation()

    def send_read_notification(self, database_directory):
        """
        helper method for sending the read notifcations from the notification
        protocol
        :param database_directory: the path to the database
        :return:
        """
        self._notification_protocol.send_read_notification(database_directory)

    def send_start_notification(self):
        """
        helper method for sending the start notifcations from the notification
        protocol
        :return:
        """
        self._notification_protocol.send_start_notification()

    def stop(self):
        """
        ends the nofitication protocol
        :return:
        """
        logger.debug("[data_base_thread] Stopping")
        self._notification_protocol.close()

