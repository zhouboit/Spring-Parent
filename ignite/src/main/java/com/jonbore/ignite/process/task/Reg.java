package com.jonbore.ignite.process.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reg {
    public static void main(String[] args) {
        Pattern compile = Pattern.compile("^[-+]?[0-9]*\\.[0-9]{1}$");
        Matcher matcher = compile.matcher("1.5");
        System.out.println(matcher.find());
    }
}
