package com.melvin.apps.materialtests;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Melvin on 2/6/2015.
*/
public class Password {

    public int calculateStrength(String password) {
        boolean sawUpper = false;
        boolean sawLower = false;
        boolean sawDigit = false;
        boolean sawSpecial = false;
        boolean sawLength = false;
        int currentScore = 0;
        // The first time the length passes 6, we increment the score.
        if (password.length() > 6)
            currentScore += 1;
            //sawLength = true;

        // Do this as efficiently as possible.
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!sawSpecial && !Character.isLetterOrDigit(c)) {
                currentScore += 1;
                sawSpecial = true;
            } else {
                if (!sawDigit && Character.isDigit(c)) {
                    currentScore += 1;
                    sawDigit = true;
                } else {
                    if (!sawUpper || !sawLower) {
                        if (Character.isUpperCase(c))
                            sawUpper = true;
                        else
                            sawLower = true;
                        if (sawUpper && sawLower)
                            currentScore += 1;
                    }
                }
            }
        }
        return currentScore;
    }
//    public List<String> status(Password password)
//    {
//        List<String> result = new ArrayList<String>();
//        //int i = -1;
//        if (!password.sawDigit)
//        {
//            result.add("Numbers".toLowerCase());
//        }
//        if (!password.sawSpecial)
//        {
//            result.add("Special Characters".toLowerCase());
//        }
//        if (!password.sawUpper)
//        {
//            result.add("Upper case".toLowerCase());
//        }
//        if (!password.sawLower)
//        {
//            result.add("Lower case".toLowerCase());
//        }
//        if (!password.sawLength)
//        {
//            result.add("Minimum seven characters".toLowerCase());
//        }
//        return result;
//    }
}

