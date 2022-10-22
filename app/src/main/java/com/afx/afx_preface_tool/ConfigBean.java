package com.afx.afx_preface_tool;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class ConfigBean {
    @JsonProperty
    boolean auto_ap = false;

    @JsonProperty
    String ap_name = "GEELY_PREFACE_NB";

    @JsonProperty
    String ap_password = "123456789";

    @JsonProperty
    Set<String> auto_start_apps = new HashSet<>();
}
