package com.rdc.musicplayer.musicplayer.bean;

import java.util.List;

/**
 * 评价bean
 */
public class EvaluateBean {

    public String ret;
    public EvaluateData data;

    public static class EvaluateData {
        public List<Evaluate> items;
        public static class Evaluate {
            public String message_content;
        }
    }

}
