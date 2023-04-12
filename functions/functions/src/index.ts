import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

export const phoneCall = functions.https.onCall(async (data, context) => {
  const {phoneNumber, contactName, time} = data;

  const body =
    `You had a new call from ${contactName || phoneNumber} at ${time}.`;
  const message: admin.messaging.TopicMessage = {
    notification: {
      title: "New phone call!",
      body: body,
    },
    android: {
      priority: "high",
    },
    topic: "phoneEvents",
  };

  try {
    await admin.messaging().send(message);
    return {success: true};
  } catch (error) {
    console.error("Error sending FCM message:", error);
    throw new functions.https.HttpsError(
      "internal",
      "Failed to send notification"
    );
  }
});

export const lowBattery = functions.https.onCall(async (data, context) => {
  const {time} = data;
  const body = `Device had low battery at ${time}!`;
  const message: admin.messaging.TopicMessage = {
    notification: {
      title: "New phone event!",
      body: body,
    },
    android: {
      priority: "high",
    },
    topic: "phoneEvents",
  };

  try {
    await admin.messaging().send(message);
    return {success: true};
  } catch (error) {
    console.error("Error sending FCM message:", error);
    throw new functions.https.HttpsError(
      "internal",
      "Failed to send notification"
    );
  }
});
