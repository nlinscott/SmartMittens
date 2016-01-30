#define BLE_DEBUG 1

#include <Wire.h>

#include "SPI.h"
#include "lib_aci.h"
#include "aci_setup.h"
#include "uart_over_ble.h"
#include "services.h"

#include "BMA250.h"
#include "L3G4200D.h"

uint8_t ble_rx_buffer[21];
uint8_t ble_rx_buffer_len = 0;

//when using this project in the Arduino IDE, delete the following include and rename UART.h to UART.ino
#include "UART.h"

//register rotation
BMA250* accel = new BMA250();

//register speed
L3G4200D* gyro = new L3G4200D();

int upYthresh = -1500;

int downYthresh = 1500;

int v = 300;

void setup(void)
{
  Serial.begin(38400);
  BLEsetup();
  Wire.begin();
  
  accel->begin(BMA250_range_4g, BMA250_update_time_64ms);
  gyro->initialize(2000);
}

void loop() {
  aci_loop();//Process any ACI commands or events
  
  accel->read();
  gyro->read();
  
   
 if(ble_rx_buffer_len){
    
    Serial.println(ble_rx_buffer_len);
    Serial.println((char*)ble_rx_buffer);
    uint8_t sendBuffer[20]="test";
    uint8_t length=5;
    lib_aci_send_data(PIPE_UART_OVER_BTLE_UART_TX_TX, sendBuffer, length);
    ble_rx_buffer_len=0;
    
  }   
  
  
  Serial.print("ACCEL X:");
  Serial.print(accel->getX());
  
  Serial.print(" Y:");
  Serial.print(accel->getY());
  
  Serial.print(" Z:");
  Serial.print(accel->getZ());
  
  Serial.println();
  
  /*
  Serial.print("GRYO X:");
  Serial.print(gyro->getX());
  
  Serial.print(" Y:");
  Serial.print(gyro->getY());
  
  Serial.print(" Z:");
  Serial.print(gyro->getZ());
  */
  
  
  if(gyro->getY() <= upYthresh){
    
    if(accel->getZ() >= v){
      Serial.print("swipe up");
       uint8_t buffer[3] = "up";
       sendData(buffer);
      delay(1200);
    }
  }else if(gyro->getY() >= downYthresh){
    
    if(accel->getZ() >= v){
       Serial.print("swipe down");
       uint8_t buffer[5] = "down";
       sendData(buffer);
      delay(1200);
    }
  }
  
  Serial.println();
  
}

void sendData(uint8_t* sendBuffer){
  uint8_t length = strlen((char*)sendBuffer);
  uart_tx(sendBuffer, length);
}



