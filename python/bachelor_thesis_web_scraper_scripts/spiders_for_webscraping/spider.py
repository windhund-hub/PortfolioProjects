#for confernences
import scrapy
from scraper.items import Title
from pymongo import MongoClient
from scrapy import Request
from scrapy.utils.project import get_project_settings
from scrapy_splash import SplashRequest


class Spider(scrapy.Spider):
    xpath_collection_name = 'xpaths'
    collection_name = 'titles'
    name = 'titles'
    start_urls = []
    settings = get_project_settings()

    #TODO: check if database exists
    def __init__(self):
        self.mongo_uri = self.settings.get('MONGO_URI')
        self.mongo_db = self.settings.get('MONGO_DATABASE')
        self.client = MongoClient(self.mongo_uri)
        self.db = self.client[self.mongo_db]
        self.collection = self.db[self.xpath_collection_name]
        self.documents = self.collection.find()
        for url in self.documents:
            self.start_urls.append(url.get("_id"))

    def start_requests(self):
        for url in self.start_urls:
            #print('____________________________________________')
            yield SplashRequest(url, self.parse, args={'wait': 2.0})
            #, args={'wait': 3.0}

    def parse(self, response):
        site_item = Title()
        document = self.collection.find_one({'_id': response.url})    
        values = str(document.get('sites'))
        sites = response.xpath(values)
        for site in sites:
            title = str(document.get('title'))
            speaker = str(document.get('speaker'))
            site_item['_id'] = site.xpath(title).get()
            site_item['speaker'] = site.xpath(speaker).get()
            site_item['event'] = document.get('event')
            site_item['year'] = document.get('year')
            yield site_item