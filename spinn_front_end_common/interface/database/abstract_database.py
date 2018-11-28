from six import add_metaclass
from spinn_utilities.abstract_base import (
    AbstractBase, abstractmethod, abstractproperty)


@add_metaclass(AbstractBase)
class AbstractDatabase(object):
    """
    This API seperates the required database calls from the implementation.

    Methods here are designed for the convenience of the caller not the
        database.

    There should only ever be a single Database Object in use at any time.
        In the case of application_graph_changed the first should closed and
        a new one created.

    Do not assume that just because 2 database objects where opened with the
        same parameters (for example sqllite file) that they hold the same data.
        In fact the second init is allowed to delete any previous data.

    While not recommended implementation objects are allowed to hold data in
        memory, with the exception of data required by the java which must be
        in the database once commit is called.
    """

    __slots__ = ()

    @abstractmethod
    def commit(self):
        """
        Ensures that all data expected is sent to the database and is peristant.

        Note: There is currently no transaction support so data may well be written
            and persistant before this call is made.
        """

    @abstractmethod
    def close(self):
        """
            Signals that the database can be closed and will not be reused.

            Once this is called any other method in this API is allowed to
                raise any kind of exception.
        """

    @abstractmethod
    def store_data_in_region_buffer(self, x, y, p, region, data):
        """ Store some information in the correspondent buffer class for a\
            specific chip, core and region

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param region: Region containing the data to be stored
        :type region: int
        :param data: data to be stored
        :type data: bytearray
        """

    @abstractmethod
    def get_region_data(self, x, y, p, region):
        """ Get the data stored for a given region of a given core

        :param x: x coordinate of the chip
        :type x: int
        :param y: y coordinate of the chip
        :type y: int
        :param p: Core within the specified chip
        :type p: int
        :param region: Region containing the data
        :type region: int
        :return: an array contained all the data received during the\
            simulation, and a flag indicating if any data was missing
        :rtype: (bytearray, bool)
        """

    @abstractmethod
    def clear(self, x, y, p, region_id):
        """ Clears the data from a given data region (only clears things\
            associated with a given data recording region).

        Warning: This method will be removed when the database moves to
            keeping data after reset.
        :param x: placement x coordinate
        :param y: placement y coordinate
        :param p: placement p coordinate
        :param region_id: the recording region ID to clear data from
        :rtype: None
        """
