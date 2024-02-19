from pymongo import MongoClient
import matplotlib.pyplot as plt
import numpy as np

# connection to database
client = MongoClient('mongodb://127.0.0.1:27017')
db = client['Scraper']
phases=["Requirement Engineering", "Architecture Design", "Project Management","Data Management", "Testing", "Model Development",
        "Model Deployment", "Integration", "AI Engineering", "AI Software Quality", "Infrastructure"]

years = ["2015","2016","2017","2018","2019","2020"]



def count_phases(collection_name):
    collection = db[collection_name]
    print("Anzahl der Titel: " + str(collection.count_documents({})))
    print()
    for phase in phases:
        print(phase + ": " + str(collection.count_documents({"categorie": {"$exists": True, "$in": [phase]}})))

    for year in years:
        if(collection.count_documents({"date": {"$exists": True}})):
            print()

            print(year + ": " + str(collection.count_documents({"date": {"$regex": year, "$options": "i"}})) + " Quellen")
            print("Anzahl der Quellen in Phasen in Jahr " + year)
            for phase in phases:
                print(phase + ": " + str(collection.count_documents({"date": {"$regex": year, "$options": "i"},"categorie": {"$exists": True, "$in": [phase]}})))
        else:
            print()

            print(year + ": " + str(collection.count_documents({"year": {"$regex": year, "$options": "i"}})) + " Quellen")
            print("Anzahl der Quellen in Phasen in Jahr " + year)
            for phase in phases:
                print(phase + ": " + str(collection.count_documents({"year": {"$regex": year, "$options": "i"}, "categorie": {"$exists": True, "$in": [phase]}})))

collections = ['blogs', 'conferences', 'papers']

for collection in collections:
    print(collection.upper())
    count_phases(collection)
    print()

