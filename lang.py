import csv
with open('elements.csv', 'rt') as f:
    reader = csv.reader(f)
    for row in reader:
        if int(row[0]) not in [1, 2, 6, 7, 8, 9, 10, 15, 16, 17, 18, 26, 35, 36, 53, 54, 79, 80, 86]:
            print("\"item.chemlib.ingot_" + row[1] + "\": \"" + row[1][0:1].upper() + row[1][1:]+" Ingot\",")

#  "item.morecharcoal.egg_charcoal": "Egg Charcoal",
