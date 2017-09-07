package com.humio.jenkinshumio.HumioConfig

import lib.FormTagLib

def f = namespace(FormTagLib)

f.section(title: descriptor.displayName) {
    f.entry(field: "serverURL", title: _("Humio Server URL"),
            help: _("The URL to the Humio Server. E.g. https://cloud.humio.com/")) {

        f.textbox()
    }

    f.entry(field: "dataspaceId", title: _("Dataspace ID"),
            help: _("The id (name) of the dataspace to store the logs in.")) {

        f.textbox()
    }

    f.entry(field: "authToken", title: _("Auth Token"),
            help: _("The Auth Token found in your Humio Account Page")) {

        f.password()
    }

    f.entry(field: "enabled", title: _("Enable Logging"), help: _("If disabled no logs will be shipped to Humio.")) {
        f.checkbox()
    }
}
