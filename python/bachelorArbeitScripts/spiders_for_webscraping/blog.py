#for conferences
import scrapy
from scrapy import Request
from scraper.items import Title
from pymongo import MongoClient
from scrapy.exceptions import IgnoreRequest
from scrapy.utils.project import get_project_settings
from scrapy_splash import SplashRequest

class Spider(scrapy.Spider):
    xpath_collection_name = 'xpaths_tester'
    collection_name = 'titles_tester'
    name = 'blog'
    start_urls = []
    settings = get_project_settings()
    pagination = '0'
    button = '0'

    def __init__(self):
        print(self.name)
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
            yield Request(url, self.parse)

    # fix code, overlapping
    def parse(self, response):
        try:
            site_item = Title()
            if(self.pagination != '0'):
                document = self.collection.find_one({'_id': self.pagination})
            else:
                document = self.collection.find_one({'_id': response.url})
            values = str(document.get('articles'))
            sites = response.xpath(values)
            for site in sites:
                title = str(document.get('title'))
                if document.get('writer'):
                    writer = str(document.get('writer'))
                    site_item['writer'] = site.xpath(writer).get()
                if document.get('date'):
                    date = str(document.get('date'))
                    site_item['date'] = site.xpath(date).get()
                site_item['_id'] = site.xpath(title).get()
                site_item['source'] = document.get('_id')
                yield site_item

            if document.get('pagination'):
                self.pagination = str(document.get('pagination'))
                next_page = response.xpath(str(document.get('next_page'))).get()
                if document.get('main_url') and next_page:
                    if(next_page != '#'):
                        next_page = str(document.get('main_url')) + response.xpath(str(document.get('next_page'))).get()
                    else:
                        next_page = None
                if next_page:
                    yield Request(next_page, self.parse)
                    #args={'wait': 1.5}
                else:
                    self.pagination = '0'
            # and self.button == '0'
            elif document.get('button'):
                self.button = '1'
                print('------------------------------------')
                    #url = response.url
                #probieren mit repeat until  da reaktion anders war und nochmal nach timeoutn schaun
                script = """
                        function main(splash)
                            local url="{url}"
                            assert(splash:go(url))
                            assert(splash:wait(2))
                            while true
                            do
                                button = splash:select("{button}")
                                if button==nil then
                                    return {
                                    html = splash:html()
                                    }
                                end
                                print(button)
                                assert(button.mouse_click())
                                assert(splash:wait(1))
                            end
                        end
                        """

                script = script.replace("{url}", response.url)
                script = script.replace("{button}", str(document.get('button')))

                yield SplashRequest(
                   response.url, self.parse, endpoint='execute', dont_filter=True,
                   args={'wait': 1, 'timeout': 3000, 'lua_source': script}
                )

            else:
                self.pagination = '0'
                self.button = '0'
        except:
            raise IgnoreRequest