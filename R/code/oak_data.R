# Get fine resolution data for a species
library(rgbif)
nrec = 100
layers = 1:3

# get the species code
# returns numerous quercus rubras!
oak <- name_suggest(q = 'quercus rubra',rank = 'species')

oak_data <- occ_data(scientificName = 'quercus rubra', hasCoordinate = TRUE, limit = nrec)

oak_data$meta
nrow(oak_data$data)

plot(oak_data$data$decimalLongitude, oak_data$data$decimalLatitude)

# Only keep ones with good accuracy
sum(is.na(oak_data$data$coordinateUncertaintyInMeters))
d <- oak_data$data
d <- d[!is.na(d$coordinateUncertaintyInMeters),]
d <- d[d$coordinateUncertaintyInMeters <= 10, ]
d$value <- 1

write.csv(d[,c('decimalLatitude','decimalLongitude', 'value')], file = 'R/presence_data/oak_data.csv', row.names = FALSE)

library(zoon)

w <- workflow(LocalOccurrenceData(filename = 'R/presence_data/oak_data.csv',
                                  columns = c(long = "decimalLongitude", lat = "decimalLatitude", value = "value")),
              Bioclim(layers = layers),
              Background(n = 1000),
              QuickGRaF,
              PrintMap)

save(w, file = 'R/zoon_temp.rdata')

library("rgdal")
class(Output(w))
# [1] "SpatialPointsDataFrame"
writeOGR(meuse, "test_geojson", layer="meuse", driver="GeoJSON")