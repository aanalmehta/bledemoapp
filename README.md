# bledemoapp

Introdction: 

  This is a demo app which scans near by bluetooth device, store data in local database, upload the data on the server and display a 
  notification for a success.

Requirements: 

  * Mobile device should support Bluetooth Hardware.
  * BLE devices should be near by Android Mobile App.
  * Local Database and backend set-up to call an API.
  
 Installation:
 
  * You can download this project and open in Android studio.
  * Sync gradle dependencies.
  
 Configuration:
  * Download the backend node server code from this URL: https://github.com/aanalmehta/ble-backend
  * Setup local database in MySql Workbench.
  * The local database details should be followed like this:
    # DB Configuration
    
    DBHOST=localhost
    
    DBUSER=root
    
    DBPASSWORD=aanal
    
    DATABASE=devices

    # Server Environment
    PORT=3000
    
  * Run below query.
  
    DROP TABLE IF EXISTS `device`;
    /*!40101 SET @saved_cs_client     = @@character_set_client */;
    /*!50503 SET character_set_client = utf8mb4 */;
    CREATE TABLE `device` (
      `id` int NOT NULL AUTO_INCREMENT,
      `uid` varchar(255) NOT NULL,
      `display_name` varchar(255) DEFAULT NULL,
      `device_name` varchar(255) DEFAULT NULL,
      `advertisement_bytes` longtext,
      `manufacturer_bytes` varchar(255) DEFAULT NULL,
      `service_uuids` varchar(255) DEFAULT NULL,
      `rssi` decimal(10,0) DEFAULT NULL,
      `event_type` decimal(10,0) DEFAULT NULL,
      `primary_phy` decimal(10,0) DEFAULT NULL,
      `secondary_phy` decimal(10,0) DEFAULT NULL,
      `advertising_sid` decimal(10,0) DEFAULT NULL,
      `tx_power` decimal(10,0) DEFAULT NULL,
      `periodic_advertising_interval` decimal(10,0) DEFAULT NULL,
      `data_status` decimal(10,0) DEFAULT NULL,
      `tx_power_level` decimal(10,0) DEFAULT NULL,
      `is_connectable` decimal(10,0) DEFAULT NULL,
      `is_legacy` decimal(10,0) DEFAULT NULL,
      PRIMARY KEY (`id`)
    )
  * Start server from the NodeJs code.
  * Once the server is running, run the Application.
  
 Maintainers:
  Aanal Shah: https://www.linkedin.com/in/aanal-mehta-shah-815ab888/
