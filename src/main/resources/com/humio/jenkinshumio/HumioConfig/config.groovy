package com.humio.jenkinshumio.HumioConfig

import lib.FormTagLib

def f = namespace(FormTagLib)

f.section(title: descriptor.displayName) {
    f.entry(field: "serverURL", title: _("Humio Server URL"),
            help: descriptor.getHelpFile()) {

        f.textbox()
    }

    f.entry(field: "dataspaceId", title: _("Dataspace ID"),
            help: descriptor.getHelpFile()) {

        f.textbox()
    }

    f.entry(field: "authToken", title: _("Auth Token"),
            help: descriptor.getHelpFile()) {

        f.password()
    }

    f.entry(field: "enabled", title: _("Enable Logging")) {
        f.checkbox()
    }
}