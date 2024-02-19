# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class Title(scrapy.Item):
    _id = scrapy.Field()
    event = scrapy.Field()
    year = scrapy.Field()
    speaker = scrapy.Field()
    writer = scrapy.Field()
    source = scrapy.Field()
    date = scrapy.Field()
