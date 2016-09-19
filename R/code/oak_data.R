# Get fine resolution data for a species
rm(list = ls())

library(rgbif)
species = 'Quercus robur'
nrec = 100000
layers = 1:19 # up to 19
res = 1000 # using 10 got rid of all records in the UK

oak_data <- occ_data(scientificName = species, hasCoordinate = TRUE, limit = nrec)

oak_data$meta
nrow(oak_data$data)

b <- oak_data$data
b <- b[!is.na(b$coordinateUncertaintyInMeters),]
b <- b[d$coordinateUncertaintyInMeters <= 1000, ]

plot(oak_data$data$decimalLongitude, oak_data$data$decimalLatitude)
plot(b$decimalLongitude, b$decimalLatitude)


# Only keep ones with good accuracy
sum(is.na(oak_data$data$coordinateUncertaintyInMeters))
d <- oak_data$data
d <- d[!is.na(d$coordinateUncertaintyInMeters),]
d <- d[d$coordinateUncertaintyInMeters <= res, ]
d$value <- 1
nrow(d)

write.csv(d[,c('decimalLatitude','decimalLongitude', 'value')], file = paste0('R/presence_data/', species, '_', res, '.csv'), row.names = FALSE)

library(zoon)

system.time({
  w <- workflow(LocalOccurrenceData(filename = 'R/presence_data/oak_data.csv',
                                    columns = c(long = "decimalLongitude", lat = "decimalLatitude", value = "value")),
                Bioclim(layers = layers, res = 2.5),
                Chain(Clean, Background(10000)),
                RandomForest,
                PrintMap(dir = 'R/predicted_occurrence/',
                         points = FALSE,
                         size = c(3200, 2400),
                         res = 300))
})

# save(w, file = 'R/zoon_temp.rdata')

writeRaster(Output(w), filename = paste0('R/predicted_occurrence/', species, '.tif'), format = 'GTiff', overwrite = TRUE)

plot(Output(w))
