import subprocess
from pymongo import MongoClient
import matplotlib.pyplot as plt
import numpy as np

# connection to database
client = MongoClient('mongodb://127.0.0.1:27017')
db = client['Scraper']
titles_global = 0
ai_titles_global = 0
labeled_titles_global = 0
phases_global = {}
years_total_ai = {}
years_phases_global = {}
#fig, ax = plt.subplots()
# qunatitative auswertung auslagern

temp = []

#couleur = ['#000000', '#dbb243', '#2e42d3', '#e54fe3', '#f23434', '#000000', '#dbb243', '#2e42d3', '#e54fe3', '#f23434','#000000']

years = ["2015","2016","2017","2018","2019","2020","2021"]
# first label-phase for AI-Identification
case_sensitive_words = ["\"AI\"", "\"ML\"", "\"DL\"", "\"NLP\""]
# nlp ausschreiben, computer vision
words = ["\"Data Science\"", "\"Data Engineering\"", "\"Artificial Intelligence\"", "\"Deep Learning\"", "\"Machine Learning\"", "\"Künstliche Intelligenz\"",
         "\"intelligente Systeme\"", "\"intelligent system\"", "\"Pandas\"", "\"apache arrows\"", "\"polygon\"", "\"apache airflow\"", "\"tensorflow\"",
         "\"keras\"", "\"pymc3\"", "\"scikit\"", "\"Diffprivlib\"", "\"dask\"", "\"apache parquet\"", "\"Pytorch\"", "\"spaCy\"", "\"plone\"", "\"Theano\"",
         "\"Caffe\"", "\"MxNet\"", "\"CNTK\"", "\"OpenNN\"", "\"ml.net\""]
#ambigious_words =[]

# second label-phase for SE-Identification
categories = {
    #wörter mit logischem and umsetzen
    # 'Data Wrangling' data or wrangling in title
    #\"Data Wrangling\" data science in title
    #{ $text: { $search: "\"Data\" \"Wrangling\"" } data and wrangling in title
    #basierend auf se for ai and ml-software: slr -> Nascimento

    "Requirement Engineering": ["\"business\" \"metric\"", "\"conflict\" \"requirements\"", "customer", "requirement",
                                              "\"real user data\"", "specification", "expectation", "\"requirement\" \"engineer\""],

    "Architecture Design": ["Architecture", "design", "\"Risk\" \"factors\"", "\"large scale\" \"machine\"","\"putting theory\" \"practice\""],

    "Project Management": ["\"Project Management\"", "Communication", "Competence", "Cultur", "Estimation", "Feedback",
                           "Organization", "\"Process management\"", "\"Risk management\"", "workflow", "manager", "agile", "\"Apache Airflow\"",
                           "\"data science\" \"projects\"", "\"data science\" \"team\"", "\"agile\" \"data science\"", "\"developer\" \"team\"",
                           "\"ml\" \"team\"", "\"organizational values\"", "\"data science\" \"workflow\"", "\"ai\" \"projects\"", "\"ai\" \"customer\"",
                           "\"ai\" \"enterprise\"", "\"managing\" \"machine learning\"", "\"potentials pitfalls\" \"ai\""],

    "Data Management": ["\"Data\" \"Management\"", "Collecting", "\"Data\" \"availability\"", "\"Data\" \"dependency\"", "\"Data\" \"quality\"",
                        "\"data\" \"drift\"", "\"Slicing\" \"data\"", "\"Integrating\" \"data\"", "\"input\" \"data\"", "\"Training\" \"data\"",
                        "Label", "\"data\" \"wrangling\"", "\"Data\" \"Storage\"", "Pandas","\"Apache Arrows\"", "Polygon", "Dask",
                        "\"Apache Parquet\"", "\"Data Scientist\"", "\"data science\"", "\"data analysis\"", "\"data analytics\"",
                        "\"bridging\" \"gap data\"", "\"collaborative\" \"data science\"", "\"data science\" \"code\"",
                        "\"data science\" \"distribution\"", "\"data science\" \"engineering\"", "\"data\" \"fit\" \"memory\"",
                        "\"data science\" \"pipelines\"", "\"data science\" \"platform\"", "\"data science\" \"workflow\"",
                        "\"practices\" \"data science\"", "\"productionising\" \"data science\"", "\"productionalizing\" \"data science\"",
                        "\"spatial\" \"data science\""
                        ],

    "Testing": ["Testing", "/^Bug", "\"test cases\"", "\"Data\" \"/^test\"", "Debugging", "/^Test", "\"black box\"", "Testability",
                            "DevOps", "MLOps", "AIOps", "\"fair\" \"testing\" \"ai\""],

    "Model Development": ["\"Model\" \"Development\"", "\"model\" \"input\"","\"Ethics\" \"Implementation\"", "\"Feature\" \"engineer\"",
                          "bayesian", "supervised", "unsupervised", "reinforcement", "\"model\" \"training\"", "classifier", "tensorflow", "keras", "pymc3",
                          "scikit", "Diffprivlib", "pytorch", "Caffe", "MxNet", "CNTK", "OpenNN", "\"data\" \"Dependency\" \"management\"",
                          "\"model\" \"behaviour\"", "\"model\" \"behavior\"", "DevOps", "MLOps", "AIOps", "\"model\" \"pipelines\"", "algorithmn",
                          "\"dataframe\" \"pipeline\"", "\"scalable\" \"data science\"", "\"data analysis\"", "\"distributed\" \"deep learning\"",
                          "\"bayesian\" \"deep learning\"", "\"data analytics\"", "\"gradient boosting\"", "\"data science\" \"model\"",
                          "\"data science\" \"workloads\"", "\"fitting\" \"model|", "\"image classification\"", "\"model\" \"evaluation\"",
                          "\"model\" \"evaluation\" \"metrics\"", "\"statistical\" \"machine learning\""],

    "Model Deployment": ["\"model\" \"deployment\"","\"model\" \"production\"","DevOps", "MLOps", "AIOps","\"continuous\" \"deployment\"",
                         "Deployment", "pipeline", "Automation", "Deployment", "DevOps", "MLOps", "AIOps", "\"ml\" \"production\"", "\"applying\" \"ai\""],

    "Integration": ["\"continuous\" \"integration\"","Integration", "DevOps", "MLOps", "AIOps", "CI/CD"],

    "Operation Support": ["\"Operation\" \"Support\"", "Monitoring", "DevOps", "MLOps", "AIOps", "CI/CD", "\"continuous\" \"delivery\"",
                          "\"monitoring\" \"machine learning\""],
#mit not model,
    "AI Engineering": ["DevOps", "MLOps", "AIOps","\"continuous\" \"engineering\"", "\"development\" \"process\"", "Pandas", "\"apache arrows\"",
                       "polygon", "apache airflow", "tensorflow", "keras", "pymc3", "libraries", "library", "scikit", "Diffprivlib", "dask",
                       "jupyter", "azure", "\"apache parquet\"", "Pytorch", "spaCy", "plone", "Theano", "Caffe", "MxNet", "CNTK", "OpenNN", "Python",
                       "\"deploying\" \"machine learning\"", "\"intelligente\" \"apps\" \"entwickeln\"", "\"ml\" \"platform\"",
                       "\"ml\" \"service\"", "\"ml\" \"workload\"", "\"building\" \"lean\" \"ai\"",
                       "\"data science\" \"engineering\"", "\"data science\" \"platform\"", "\"deep learning\" \"framework\"",
                       "\"machine learning\" \"framework\"", "\"deep learning\" \"intel\"",
                       "\"deep learning\" \"java\"", "\"deep learning\" \"library\"", "\"deep learning\" \"pipeline\"", "k8s",
                       "\"ai\" \"building\"", "\"ai\" \"software\"", "\"docker\" \"data science\"",
                       "\"large scale\" \"machine\"", "\"processing\" \"machine learning\"", "\"putting\" \"theory\" \"practice\"",
                       "\"nodejs\" \"ml\""],

    "AI Software Quality": ["Quality", "Interpretability", "ethics", "Complexity", "Efficiency", "Fairness", "Imperfection", "Reliability",
                "\"Data\" \"/test\"", "Robustness", "\"Safety\" \"Stability|", "Scalability", "Stability", "Staleness", "verification",
                "Verifiability", "validation", "bias", "Failure", "Fault", "Performance", "\"Cost\" \"Effort\"", "Privacy", "Theano", "scalable",
                "\"bias\" \"machine learning\"", "\"high performance\" \"data\"", "\"algorithmic\" \"bias\"", "\"building robust\"",
                "\"building resilient\"", "graal", "\"model\" \"evaluation\"", "\"model\" \"evaluation\" \"metrics\"",
                "\"quantifying\" \"uncertainty\" \"machine\"", "\"sustainable\" \"machine learning\"", "\"performance\" \"tuning\""
                            ],

    "Infrastructure":["Infrastructure", "\"Technical Debt\"", "\"Configuration debt\"", "\"Data\" \"management\" \"tool\"", "Logging", "Platform",
                      "\"Resource\" \"limitation\"", "\"Support\" \"tool\"","Azure", "aws", "\"data science\" \"tools\"", "\"data science\" \"platform\"",
                      "\"data science\" \"workloads\"", "k8s", "\"serverless machine\"", "\"docker\" \"data science\""
                      ]
    #aufteilen"Model Management": ["Module documentation"],
    # aufteilen
    #"Integration & Deployment": []
    #aufteilem
    #"Development & Tools":[],
}
# init
for categorie in categories.keys():
    phases_global[categorie] = 0
    #temp.append(categorie)
'''
phases=tuple(temp)
y_pos = np.arange(len(phases))
couleur = ['#000000', '#dbb243', '#2e42d3', '#e54fe3', '#f23434', ]
'''
for year in years:
    years_total_ai[year] = 0
    years_phases_global[year] = {}


def label_titles(collection_name):
    collection = db[collection_name]
    global titles_global
    global ai_titles_global
    global labeled_titles_global
    global phases_global
    global years_total_ai
    years_collection_ai = {}
    years_phases = {}
    for year in years:
        years_collection_ai[year] = 0
        years_phases[year] = {}
    #{ "date": { "$regex": "2015|2016|2017|2018|2019|2020|2021", "$options": "i" } }
    collection.update_many({"area":"AI"}, {"$unset": {"categorie":1, "area":1}})

    # Tagging for AI
    for w in case_sensitive_words:
        #print(collection.count_documents({"$text": {"$search": w, "$caseSensitive": True}}))
        collection.update_many({"date": { "$regex": "2015|2016|2017|2018|2019|2020|2021", "$options": "i" }, "$text": {"$search": w, "$caseSensitive": True}}, {"$set": {"area": 'AI', "categorie":["Unknown"]}})


    for w in words:
         #print(collection.count_documents({"$text": {"$search": w, "$caseSensitive": True}}))
        collection.update_many({"date": { "$regex": "2015|2016|2017|2018|2019|2020|2021", "$options": "i" }, "$text": {"$search": w}}, {"$set": {"area": 'AI',"categorie":["Unknown"]}})

    for year in years_total_ai.keys():
        years_total_ai[year] = years_total_ai.get(year) + collection.count_documents({"date": { "$regex": year}, "area": 'AI'})

    for year in years_collection_ai.keys():
        years_collection_ai[year] = collection.count_documents({"date": { "$regex": year}, "area": 'AI'})


    for categorie in categories.keys():
        for keyword in categories[categorie]:
             if len(categories[categorie]) > 0:
                collection.update_many({"area":'AI',"categorie":"Unknown","$text":{'$search' : keyword}}, {"$set": {"categorie":[categorie]}})
                collection.update_many({"area":'AI', "categorie": { "$nin": ["Unknown"] },"$text":{'$search' : keyword}},{"$addToSet": {"categorie": categorie}})
               #collection.update_many({"area":'AI',"categorie":"Unknown","name":{'$regex' : keyword, '$options' : 'i'}}, {"$set": {"categorie":[categorie]}})
                #collection.update_many({"area": 'AI', "categorie": { "$nin": ["Unknown"] },"name": {'$regex': keyword, '$options': 'i'}},{"$addToSet": {"categorie": categorie}})

    titles = collection.count_documents({})
    titles_global = titles_global + titles
    ai_titles = collection.count_documents({"area": "AI"})
    ai_titles_global = ai_titles_global + ai_titles
    labeled_titles = collection.count_documents({"categorie": {"$exists": True, "$nin": ["Unknown"] }})
    labeled_titles_global = labeled_titles_global + labeled_titles

    #for categorie in categories.keys():
        #print(categorie + ": " + str(collection.count_documents({"categorie": categorie})))
    #    labeled_titles = labeled_titles + collection.count_documents({"categorie": categorie})

    percentage_title = ai_titles*100/titles
    percentage_label = labeled_titles*100/ai_titles

    print("Titles: " + str(titles))
    print("AI-Titles: " + str(ai_titles) + " | " + str(percentage_title.__round__(1)) + " %")
    print("Labeled Titles: " + str(labeled_titles) + " | " + str(percentage_label.__round__(1)) + " %")

    print("percentage phases:")
    numbers_phases = []

    for categorie in categories.keys():
        count_cat = collection.count_documents({"categorie": categorie})
        #numbers_phases.append(count_cat)

        for year in years_phases.keys():
            count_cat_year = collection.count_documents({"categorie": categorie, "date": {"$regex": year}})
            years_phases[year].update({categorie: count_cat_year})
            if years_phases_global[year].get(categorie) is None:
                new_count_cat_year = 0 + count_cat_year
            else:
                new_count_cat_year = years_phases_global[year].get(categorie) + count_cat_year
            years_phases_global[year].update({categorie: new_count_cat_year})
        percentage_phase = count_cat*100/labeled_titles
        print(categorie + ": " + str(count_cat) + " -------------------------> " + str(percentage_phase.__round__(1)) + " %")
        phases_global[categorie] = phases_global.get(categorie) + count_cat

    for year in years_collection_ai.keys():
        print(year + ": " + str(years_collection_ai.get(year)))
    for year in years_phases.keys():
        print(year + " Phases: " + str(years_phases.get(year)))
    '''
    plt.title('SE-Phasen')
    plt.ylabel('Menge')
    plt.xlabel("Phasen")
    plt.bar(y_pos, numbers_phases, align='center', alpha=0.6, color=couleur)
    ax.set_xticks(range(len(phases)))
    ax.set_xticklabels(phases, rotation='vertical')
    plt.show()
    '''

def paper_phases(collection_name):
    collection = db[collection_name]
    global titles_global
    global ai_titles_global
    global labeled_titles_global
    global phases_global
    years_collection_ai = {}
    years_phases = {}

    #anzahl der angeschuaten papers
    ai_titles = collection.count_documents({"area": "AI"})
    titles_global = titles_global + ai_titles
    ai_titles_global = ai_titles_global + ai_titles
    labeled_titles = collection.count_documents({"categorie": { "$nin": ["Unknown"] }})
    labeled_titles_global = labeled_titles_global + labeled_titles

    for year in years:
        years_collection_ai[year] = 0
        years_phases[year] = {}


    for year in years_collection_ai.keys():
        years_collection_ai[year] = collection.count_documents({"date": { "$regex": year}, "area": 'AI'})


    percentage_label = labeled_titles * 100 / ai_titles
    print("Titles" + ": " + str(ai_titles))
    print("Ai-Titles" + ": " + str(ai_titles))
    print("Labled Titles: " + str(labeled_titles) + " | " + str(percentage_label.__round__(1)) + " %")

    print("percentage phases:")
    numbers_phases = []
    for categorie in categories.keys():
        count_cat = collection.count_documents({"categorie": categorie})
        #numbers_phases.append(count_cat)
        for year in years_phases.keys():
            count_cat_year = collection.count_documents({"categorie": categorie, "date": { "$regex": year}})
            years_phases[year].update({categorie:count_cat_year})
            new_count_cat_year = years_phases_global[year].get(categorie) + count_cat_year
            years_phases_global[year].update({categorie: new_count_cat_year})

        percentage_phase = count_cat * 100 / labeled_titles
        print(categorie + ": " + str(count_cat) + " ------> " + str(
            percentage_phase.__round__(1)) + " %")
        phases_global[categorie] = phases_global.get(categorie) + count_cat


    for year in years_collection_ai.keys():
        print(year + " AI: " + str(years_collection_ai.get(year)))

    for year in years_phases.keys():
        print(year + " Phases: " + str(years_phases.get(year)))

    '''
    plt.title('SE-Phasen Papers')
    plt.ylabel('Menge')
    plt.xlabel("Phasen")
    plt.bar(y_pos, numbers_phases, align='center', alpha=0.6, color=couleur)
    ax.set_xticks(range(len(phases)))
    ax.set_xticklabels(phases, rotation='vertical')
    plt.show()
    '''

collections = ['titles', 'titles_tester']

for collection in collections:
    label_titles(collection)

paper_collection = 'paper'
paper_phases(paper_collection)

percentage_ai_titles_global = ai_titles_global*100/titles_global
percentage_labeled_titles_global = labeled_titles_global*100/ai_titles_global
print("-----------------------------Summary-----------------------------")
print("Titles: " + str(titles_global))
print("AI Titles: " + str(ai_titles_global) + " | " + str(percentage_ai_titles_global.__round__(1)) + " %")
print("Labeled Titles: " + str(labeled_titles_global) + " | " + str(percentage_labeled_titles_global.__round__(1)) + " %")
for phase in phases_global.keys():
    count_phase = phases_global.get(phase)
    percentage_phase_global = count_phase * 100 / labeled_titles_global
    print(phase + ": " + str(count_phase) + " -------------------------> " + str(percentage_phase_global.__round__(1)) + " %" )


for year in years_total_ai.keys():
    print(year + ": " + str(years_total_ai.get(year)))

for year in years_phases_global.keys():
    print(year + ": " + str(years_phases_global.get(year)))



    #labeled_titles = labeled_titles + collection.count_documents({"categorie": categorie})

#print(collection.count_documents({"area": 'AI'}))
#docs = collection.count_documents({"area": 'AI'})
#events = collection.distinct("event",{"area": 'AI'})
#for event in events:
#    print(event + ':' + str(collection.count_documents({"event": event, "area": 'AI'})))
   # print(collection.count_documents({"event": event, "area": 'AI'}))



