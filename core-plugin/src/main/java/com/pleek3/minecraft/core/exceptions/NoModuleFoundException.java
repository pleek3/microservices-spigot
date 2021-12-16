package com.pleek3.minecraft.core.exceptions;

public class NoModuleFoundException extends Exception {

    public NoModuleFoundException(String fileName) {
        System.out.println("[Modules] " + fileName + " cannot be loaded as module!");
    }
}
