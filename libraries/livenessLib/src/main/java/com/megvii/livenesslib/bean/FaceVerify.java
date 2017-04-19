package com.megvii.livenesslib.bean;

/**
 * Created by wrf on 2016/10/31.
 */

public class FaceVerify {


    /**
     * time_used : 2022
     * result_ref1 : {"confidence":88.195}
     * request_id : 1477881327,c95834c8-3e86-4945-97fe-7a6e44286b59
     */

    private int time_used;
    /**
     * confidence : 88.195
     */

    private ResultRef1Bean result_ref1;
    private String request_id;

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public ResultRef1Bean getResult_ref1() {
        return result_ref1;
    }

    public void setResult_ref1(ResultRef1Bean result_ref1) {
        this.result_ref1 = result_ref1;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public static class ResultRef1Bean {
        private double confidence;

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
    }
}
