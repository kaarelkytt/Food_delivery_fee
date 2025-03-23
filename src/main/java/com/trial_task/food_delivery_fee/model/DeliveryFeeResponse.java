package com.trial_task.food_delivery_fee.model;

/**
 * Represents the response for a delivery fee calculation request.
 */
public class DeliveryFeeResponse {
    private final String status;
    private final DeliveryFee deliveryFee;

    /**
     * Constructs a DeliveryFeeResponse object with the given status and delivery fee.
     *
     * @param status      The status of the response, indicating success or error.
     * @param deliveryFee The calculated delivery fee, or null if there was an error.
     */
    public DeliveryFeeResponse(String status, DeliveryFee deliveryFee) {
        this.status = status;
        this.deliveryFee = deliveryFee;
    }

    public DeliveryFeeResponse(String status) {
        this(status, null);
    }

    public String getStatus() {
        return status;
    }

    public DeliveryFee getDeliveryFee() {
        return deliveryFee;
    }
}
