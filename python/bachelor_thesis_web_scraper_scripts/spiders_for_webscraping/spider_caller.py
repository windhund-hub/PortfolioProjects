import subprocess

spiders = ['spider']
for spider in spiders:
    myprocess = subprocess.Popen(["scrapy","crawl", spider], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    output, error = myprocess.communicate()
    status = myprocess.wait()
