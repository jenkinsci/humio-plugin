package org.jenkinsci.plugins.humio.HumioConfig

import lib.FormTagLib

def f = namespace(FormTagLib)

f.section(title: descriptor.displayName) {
    f.entry(field: "serverURL", title: _("Humio Server URL"),
            help: _("The URL to the Humio Server. E.g. https://cloud.humio.com/")) {

        f.textbox()
    }

    f.entry(field: "repositoryName", title: _("Repository Name"),
            help: _("The name of the repository to store the logs in.")) {

        f.textbox()
    }

    f.entry(field: "ingestToken", title: _("Ingest Token"),
            help: _("The Ingest Token found in your Repository's Settings Page")) {

        f.password()
    }

    f.entry(field: "enabled", title: _("Enable Logging"), help: _("If disabled no logs will be shipped to Humio.")) {
        f.checkbox()
    }
}
