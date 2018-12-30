package net.hang321.sample.app.protobufdemo;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;

import net.hang321.sample.protobuf.data.proto.MoMsgProtos;
import net.hang321.sample.protobuf.data.proto.MtMsgProtos.MtMessage;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by steve.
 */

public class MainActivity extends AppCompatActivity {

  private HistoryAdapter mAdapter;

  MqttAndroidClient mqttAndroidClient;

  // edit these parameters
  final String serverUri = "tcp://iot.eclipse.org:1883";
  final String clientId = "a3e3b0af-af85-4633-a0f5-0b10212bdabd";
  final String subscriptionTopic = "demo/mt/request/" + clientId;
  final String publishTopic = "demo/mo/request/" + clientId;
  final String publishMessage = "Hello World!";
  // end of section

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FloatingActionButton fab = findViewById(R.id.floatingActionButton);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        publishMessage();
      }
    });

    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.history_recycler_view);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    mAdapter = new HistoryAdapter(new ArrayList<String>());
    mRecyclerView.setAdapter(mAdapter);

    mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
    mqttAndroidClient.setCallback(new MqttCallbackExtended() {
      @Override
      public void connectComplete(boolean reconnect, String serverURI) {

        if (reconnect) {
          addToHistory("Reconnected to : " + serverURI);
          // Because Clean Session is true, we need to re-subscribe
          subscribeToTopic();
        } else {
          addToHistory("Connected to: " + serverURI);
        }
      }

      @Override
      public void connectionLost(Throwable cause) {
        addToHistory("The Connection was lost.");
      }

      @Override
      public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
          // Decode from protobuf
          MtMessage mtMessage = MtMessage.parseFrom(message.getPayload());
          String sender = mtMessage.getSender();
          String content = mtMessage.getText();

          addToHistory("Incoming message: " + sender + ":: " + content);
        } catch (InvalidProtocolBufferException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void deliveryComplete(IMqttDeliveryToken token) {

      }
    });

    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setAutomaticReconnect(true);
    mqttConnectOptions.setCleanSession(false);


    try {
      //addToHistory("Connecting to " + serverUri);
      mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
          disconnectedBufferOptions.setBufferEnabled(true);
          disconnectedBufferOptions.setBufferSize(100);
          disconnectedBufferOptions.setPersistBuffer(false);
          disconnectedBufferOptions.setDeleteOldestMessages(false);
          mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
          subscribeToTopic();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          addToHistory("Failed to connect to: " + serverUri);
        }
      });


    } catch (MqttException ex) {
      ex.printStackTrace();
    }

  }

  private void addToHistory(String mainText) {
    System.out.println("LOG: " + mainText);
    mAdapter.add(mainText);
    Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement

    return super.onOptionsItemSelected(item);
  }

  public void subscribeToTopic() {
    try {
      mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          addToHistory("Subscribed!");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          addToHistory("Failed to subscribe");
        }
      });


      mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          // message Arrived!
          System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
          try {
            // Decode from protobuf
            MtMessage mtMessage = MtMessage.parseFrom(message.getPayload());
            String sender = mtMessage.getSender();
            String content = mtMessage.getText();

            addToHistory("Incoming message: " + sender + ":: " + content);
          } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
          }
        }
      });

    } catch (MqttException ex) {
      System.err.println("Exception whilst subscribing");
      ex.printStackTrace();
    }
  }

  public void publishMessage() {

    try {
      // create protobuf message
      UUID uuid = UUID.fromString(clientId);

      MoMsgProtos.MoMessage moMessage = MoMsgProtos.MoMessage.newBuilder()
          .setDateTime(Timestamp.newBuilder().setSeconds(System.currentTimeMillis()/1000).build())
          .setInstanceIdMsb(uuid.getMostSignificantBits())
          .setInstanceIdLsb(uuid.getLeastSignificantBits())
          .setMsgId(System.currentTimeMillis()) // sqlite db id. Use timestamp instead as example only.
          .setRecipient("someone")
          .setText(publishMessage)
          .build();
      byte[] moBytes = moMessage.toByteArray();

      MqttMessage message = new MqttMessage();
      message.setPayload(moBytes);

      mqttAndroidClient.publish(publishTopic, message);
      addToHistory("Message Published");
      if (!mqttAndroidClient.isConnected()) {
        addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
      }
    } catch (MqttException e) {
      System.err.println("Error Publishing: " + e.getMessage());
      e.printStackTrace();
    }
  }

}