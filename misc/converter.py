#!/usr/bin/python3
# -*- coding: utf-8 -*-
import sys
import codecs

print ("===== Neelix Converter 1.2 (01.09.15) =====");
print ("Konvertiert eine mit 'IT-Support-Statistik' erstellten Datei in ein für Neelix lesbares Format.")

if len(sys.argv) != 3:
    sys.exit("Usage: converter.py <inputfile> <outputfile>");
old = codecs.open(sys.argv[1], "r", "utf-8");
print ("Input: " + sys.argv[1]);
new = codecs.open(sys.argv[2], "w", "utf-8");
print ("Output: " + sys.argv[2]);
for line in old:
    if not line.strip():
        continue;
    splitted = line.strip().split(" # ");
    if "misc" in splitted[2]:
        splitted[2] = "OTHER";
    if "null" in splitted[3]:
        splitted[3] = "";
    elif ";" in splitted[3]:
        splitted[3] = splitted[3].replace(";", ":");
    if "null" in splitted[4]:
        splitted[4] = "RZ";
    elif "tb4(lokal)" in splitted[4]:
        splitted[4] = "TB4LOCAL";
    elif "tb4(tel/OTRS)" in splitted[4]:
        splitted[4] = "TB4TELOTRS";
    date = splitted[0].split(" ");
    formattedDate = date[2];
    if date[1] == "Januar":
        formattedDate += "-01-" + date[0];
    elif date[1] == "Februar":
        formattedDate += "-02-" + date[0];
    elif date[1] == "März":
        formattedDate += "-03-" + date[0];
    elif date[1] == "April":
        formattedDate += "-04-" + date[0];
    elif date[1] == "Mai":
        formattedDate += "-05-" + date[0];
    elif date[1] == "Juni":
        formattedDate += "-06-" + date[0];
    elif date[1] == "Juli":
        formattedDate += "-07-" + date[0];
    elif date[1] == "August":
        formattedDate += "-08-" + date[0];
    elif date[1] == "September":
        formattedDate += "-09-" + date[0];
    elif date[1] == "Oktober":
        formattedDate += "-10-" + date[0];
    elif date[1] == "November":
        formattedDate += "-11-" + date[0];
    elif date[1] == "Dezember":
        formattedDate += "-12-" + date[0];
    formattedRequest = formattedDate + ";" + date[3] + ";" + splitted[1] + ";" + splitted[2] + ";" + splitted[3] + ";" + \
                       splitted[4] + "\n";
    new.write(formattedRequest);
old.close();
new.close();
print ("Finished!");
