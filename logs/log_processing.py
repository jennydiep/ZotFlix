from pathlib import Path

def logProcessing(masterPath, slavePath):
# master = from master instance, slave from slave instance
    TSAverage = fileProcessing(masterPath, slavePath, "TS.txt")
    TJAverage = fileProcessing(masterPath, slavePath, "TJ.txt")

    print("TS Average: " + str(TSAverage) + " ns   TJAverage: " + str(TJAverage)
          + " ns")

def fileProcessing(masterPath, slavePath, fileName):
# helper function for logprocessing
# finds avg with two files combined
# used to combine master and slave files to compute average
    try:
        total = 0
        entries = 0;
        
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


if __name__ == "__main__":
    
    # TJ.txt contains list of times that use JDBC per query
    # TS.txt contains list of times for search servlet to run completely per query
    master = str(Path().absolute()) + '/' + "master"
    slave = str(Path().absolute()) + '/' + "slave"
    logProcessing(master, slave)
    
