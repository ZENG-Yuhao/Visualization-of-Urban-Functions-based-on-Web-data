# Visualization of Urban Functions based on Web data
Key words: `Smart City`, `Urbanization`, `k-means`, `dbscan`, `geo-location`, `PostgreSQL`, `PostGIS`, `UnfoldingMap`, `Processing`

#### Description
In 2012, with the explosive growth of the Internet and the smartphone market in China, the quantity of 'user data' generated by those SNS sites has explosively increased, "Big data on Web data" has became a hot topic. Web data were considered to be an important method to solve the urbanization problems like `Resource Allocation`, `Over-concentration of population` etc. 

For most popular SNS sites such like Flickr, Facebook, Sina Weibo etc, has their own Open API platform which can be used to access to their users' public data. When people post a tweet on a SNS site with their smartphone, this tweet will be attached to a location (`latitude`, `longitude`) except some privacy case, and will also be attached to a tag (`restaurant`, `hospital`, `residence` etc.) of the nearest POI. With these locations and tags, we can do data mining and many other studies.

The subject of this project was etablished under such circumstance.

####Overview
* [Kmeas](#kmeans)
* [DBscan](#dbscan)
* [DBscan & Kmeans](#dbscan-and-kmeans)

## Kmeans
The Shanghai City has been divided to 90+ blocks according to metro lines and the main roads, each block has a statistic feature by counting the tweets for each tag in the block, this feature can be used to do clustering analyse (K-means for example ). Each cluster (block) has a representative color.

###### Rate of Satisfaction (RS)
The degree of closeness to center of clustering for each cluster.
Generally speaking, each time when K is fixed, the RS always tends to be stable as long as there is enough executions. 
It's obvious that when K=1, the RS reach the minimum value, and when K=`number of blocks` the RS reach the maximun.

###### Choice making of K



![image](https://github.com/ZENG-Yuhao/Visualization-of-urban-functions-based-on-web-datas/blob/master/screenshots/result1.png)
![image](https://github.com/ZENG-Yuhao/Visualization-of-urban-functions-based-on-web-datas/blob/master/screenshots/result2.png)

## DBscan
![image](https://github.com/ZENG-Yuhao/Visualization-of-urban-functions-based-on-web-datas/blob/master/screenshots/dbscan2.png)

## DBscan and Kmeans
![image](https://github.com/ZENG-Yuhao/Visualization-of-urban-functions-based-on-web-datas/blob/master/screenshots/6.png)
![image](https://github.com/ZENG-Yuhao/Visualization-of-urban-functions-based-on-web-datas/blob/master/screenshots/9.png)

