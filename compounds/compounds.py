import csv

with open('compounds2.csv', 'rt') as f:
    reader = csv.reader(f)
    for row in reader:
        name = row[0]
        file = open(row[0] + ".json", "w")
        file.write("{\n")
        file.write("\"parent\": \"item/generated\",\n")
        file.write("\"textures\": {\n")
        file.write("\"layer0\": \"chemlib:items/compound\"\n")
        file.write("}\n}")
        file.close()

    #  "item.morecharcoal.egg_charcoal": "Egg Charcoal",
