import json
import re
from ingredient_parser.en import parse
with open("Food Measurements.txt") as f:
    food_measurements_list = [item.rstrip('\n') for item in list(f.readlines())]
with open("meta00015.json") as f:
    data = json.load(f)

numbers_finder = r"\d[A-Za-z]*"
extra_info_finder = r"[(][A-Za-z0-9]*"
extra_info_finder_2 = r"[A-Za-z0-9]*[)]"
filtered_ingredients = list()
print(data['ingredientLines'])
for x in data['ingredientLines']:
    result = list()
    words_list = x.split()
    for index, y in enumerate(words_list):
        if bool(re.match(extra_info_finder, y)):
            index2 = [i for i, item in enumerate(words_list[index:]) if re.search(extra_info_finder_2, item)][0]
            del words_list[index:index+index2+1]
            continue
        parsed_y = parse(y)['name']
        if not bool(re.match(numbers_finder, parsed_y)) and parsed_y not in food_measurements_list:
            result.append(parsed_y)
    filtered_ingredients.append(" ".join(result))

print(filtered_ingredients)