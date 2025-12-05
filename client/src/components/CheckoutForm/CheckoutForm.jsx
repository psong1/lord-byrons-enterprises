import React, { useState } from "react";
import { loadStripe } from "@stripe/stripe-js";
import {
  Elements,
  CardElement,
  useStripe,
  useElements,
} from "@stripe/react-stripe-js";
import axios from "axios";
import "./CheckoutForm.css";

const stripePromise = loadStripe("");

const CheckoutFormContent = ({
  shippingAddressId,
  billingAddressId,
  userToken,
}) => {
  const stripe = useStripe();
  const elements = useElements();
  const [error, setError] = useState(null);
  const [processing, setProcessing] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setProcessing(true);
    setError(null);

    if (!stripe || !elements) {
      return;
    }

    const cardElement = elements.getElement(CardElement);
    const { error: stripeError, paymentMethod } =
      await stripe.createPaymentMethod({
        type: "card",
        card: cardElement,
      });

    if (stripeError) {
      setError(stripeError.message);
      setProcessing(false);
      return;
    }

    const orderPayload = {
      shippingAddressId,
      billingAddressId,
      userToken,
    };

    try {
      const response = await axios.post(
        "http://localhost:8080/orders",
        orderPayload,
        {
          headers: {
            Authorization: `Bearer ${userToken}`,
            "Content-Type": "application/json",
          },
        }
      );
    } catch (error) {
      console.error(`Order failed: ${error}`);
      setError(error.response?.data || "Order failed. Please try again.");
    }

    setProcessing(false);
  };

  return (
    <form onSubmit={handleSubmit} className="checkout-form">
      <h3 className="checkout-title">Payment Details</h3>

      <div className="stripe-card-element">
        <CardElement options={{ hidePostalCode: true }} />
      </div>

      {error && <div className="checkout-error">{error}</div>}

      <button
        type="submit"
        disabled={!stripe || processing}
        className="pay-button"
      >
        {processing ? "Processing..." : "Pay & Place Order"}
      </button>
    </form>
  );
};

const CheckoutForm = (props) => {
  return (
    <Elements stripe={stripePromise}>
      <CheckoutFormContent {...props} />
    </Elements>
  );
};

export default CheckoutForm;
