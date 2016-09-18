# Get fine resolution data for a species
rm(list = ls())

library(rgbif)
#nrec = 100000
layers = 1:19 # up to 19

# get the species code
# returns numerous quercus rubras!
# oak <- name_suggest(q = 'quercus rubra',rank = 'species')
# 
# oak_data <- occ_data(scientificName = 'quercus rubra', hasCoordinate = TRUE, limit = nrec)
# 
# oak_data$meta
# nrow(oak_data$data)
# 
# plot(oak_data$data$decimalLongitude, oak_data$data$decimalLatitude)
# 
# # Only keep ones with good accuracy
# sum(is.na(oak_data$data$coordinateUncertaintyInMeters))
# d <- oak_data$data
# d <- d[!is.na(d$coordinateUncertaintyInMeters),]
# d <- d[d$coordinateUncertaintyInMeters <= 10, ]
# d$value <- 1
# 
# write.csv(d[,c('decimalLatitude','decimalLongitude', 'value')], file = 'R/presence_data/oak_data.csv', row.names = FALSE)

library(zoon)

system.time({
  w <- workflow(LocalOccurrenceData(filename = 'R/presence_data/oak_data.csv',
                                    columns = c(long = "decimalLongitude", lat = "decimalLatitude", value = "value")),
                Bioclim(layers = layers, res = 2.5),
                Chain(Clean, Background(10000)),
                RandomForest,
                PrintMap)
})

save(w, file = 'R/zoon_temp.rdata')

writeRaster(Output(w), filename = 'R/predicted_occurrence/oak_test.tif', format = 'GTiff', overwrite = TRUE)

plot(Output(w))