package com.headfirstlabs.nasadailyimage.iotd;

public interface IotdHandlerListener {
        
        public void iotdParsed(String url, String title, String description, String date);

}