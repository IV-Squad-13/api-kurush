package com.squad13.util;

import io.quarkus.qute.TemplateEnum;

@TemplateEnum
public enum Local {
    UNIDADES_PRIVATIVAS("Unidades Privativas"),
    AREA_COMUM("Área Comum");

    public final String label;

    Local(String label) {
        this.label = label;
    }
}
