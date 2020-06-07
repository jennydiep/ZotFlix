from pathlib import Path
import os.path
from os import path

def logProcessing(masterPath, slavePath):
# master = from master instance, slave from slave instance
    TSAverage = fileProcessing(masterPath, slavePath, "TS.txt")
    TJAverage = fileProcessing(masterPath, slavePath, "TJ.txt")

    print("TS Average: " + str(TSAverage/1000000) + " ms   TJAverage: "
          + str(TJAverage/1000000) + " ms")

def fileProcessing(masterPath, slavePath, fileName):
# helper function for logprocessing
# finds avg with two files combined
# used to combine master and slave files to compute average
    try:
        total = 0
        entries = 0
        
        master = open(masterPath + '/' + fileName, "r")
        for i in master.readlines():
            time = int(i.strip("\n"))
            total += time
            entries += 1

        slave = open(slavePath + '/' + fileName, "r")
        for j in slave.readlines():
            time = int(j.strip("\n"))
            total += time
            entries += 1

    finally:
        master.close()
        slave.close()
    
    average = total/entries
    return average

def oneInstance(path, file):
    try:
        total = 0
        entries = 0

        file = open(path + '/' + file, 'r')
        for i in file.readlines():
            time = int(i.strip('\n'))
            total += time
            entries += 1

    finally:
        file.close()
        
    average = total/entries
    return average

if __name__ == "__main__":
    
    # TJ.txt contains list of times that use JDBC per query
    # TS.txt contains list of times for search servlet to run completely per quer
    if path.exists(str(Path().absolute()) + '/' + "master"):
        master = str(Path().absolute()) + '/' + "master"
        slave = str(Path().absolute()) + '/' + "slave"
        logProcessing(master, slave)
    else:
        TSAvg = oneInstance(str(Path().absolute()), "TS.txt")
        TJAvg = oneInstance(str(Path().absolute()), "TJ.txt")

        print("TS Average: " + str(TSAvg/1000000) + " ms   TJAverage: " +
              str(TJAvg/1000000) + " ms")
    
