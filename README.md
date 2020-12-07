# openNTRIP
openNTRIP is multi-caster server for transverse RTCM data to internet.

## Installation
1. Install mysql
2. Insert table "ntrip" in the database (openNTRIP/src/main/resources/ntrip.sql)
3. Edit config file (openNTRIP/src/main/resources/ntrip.sql/db.properties)
4. Try to run

## Usage
For reference station
SOURCE: AL1 or AL2
PASSWORD: 44444

For user (if mountpoint authentication set 1)
Account: Administrator
Password: password

You can use RtkLib for retranslating gnns data to openNTRIP.

## Roadmap
1. Test user nmea processing.
2. Save rtcm stream and convert to RINEX format.
3. To implement console commands.
4. Add support RTCM 1021-1027 message.
5. Add support FKP.
6. Take over the world.
