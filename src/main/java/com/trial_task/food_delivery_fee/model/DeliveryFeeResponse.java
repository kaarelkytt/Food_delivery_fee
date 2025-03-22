package com.trial_task.food_delivery_fee.model;

public class DeliveryFeeResponse {
    private final String status;
    private final DeliveryFee deliveryFee;

    public DeliveryFeeResponse(String status, DeliveryFee deliveryFee) {
        this.status = status;
        this.deliveryFee = deliveryFee;
    }

    public DeliveryFeeResponse(String status) {
        this.status = status;
        this.deliveryFee = null;
    }

    public String getStatus() {
        return status;
    }

    public DeliveryFee getDeliveryFee() {
        return deliveryFee;
    }
}
