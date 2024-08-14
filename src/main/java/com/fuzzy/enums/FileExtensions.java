package com.fuzzy.enums;

public enum FileExtensions {

    DAT("dat"),
    INI("ini"),
    INT("int"),
    U("u"),
    DLL("dll");


    final String extension;

    FileExtensions(String extension) {
        this.extension = extension;
    }



}
