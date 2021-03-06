import requests
from spinn_machine.virtual_machine import VirtualMachine


class HBPMaxMachineGenerator(object):
    """ Generates the width and height of the maximum machine a given\
        HBP server can generate.
    """

    __slots__ = []

    def __call__(self, hbp_server_url, total_run_time):
        """
        :param hbp_server_url: \
            The URL of the HBP server from which to get the machine
        :param total_run_time: The total run time to request
        """

        max_machine = self._max_machine_request(hbp_server_url, total_run_time)

        # Return the width and height and assume that it has wrap arounds
        return VirtualMachine(
            width=max_machine["width"], height=max_machine["height"],
            with_wrap_arounds=None, version=None)

    def _max_machine_request(self, url, total_run_time):
        if url.endswith("/"):
            url = url[:-1]
        r = requests.get("{}/max".format(url), params={
            'runTime': total_run_time})
        r.raise_for_status()
        return r.json()
