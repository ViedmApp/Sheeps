package com.example.viedmapp.ocr;

import org.json.JSONObject;

public interface AsyncResponse {
    void processFinish(JSONObject jsonObject);
    void processFinish02(JSONObject js);
}
