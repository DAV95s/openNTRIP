# openNTRIP
openNTRIP is multi-caster server for streaming RTCM data to internet.

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
1. Add support RTCM 1021-1027 message.
2. Add support FKP.
