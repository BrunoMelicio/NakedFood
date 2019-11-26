import json
import re
from ingredient_parser.en import parse
from string import punctuation
from nltk.corpus import stopwords

with open("Food Measurements.txt") as f:
    food_measurements_list = [item.rstrip('\n') for item in list(f.readlines())]
    FOOD_MEASUREMENTS_SET = set(food_measurements_list)

with open("Description Words.txt") as f:
    description_words_list = [item.rstrip('\n') for item in list(f.readlines())]
    DESCRIPTION_WORDS_SET = set(description_words_list)

with open("meta00015.json") as f:
    data = json.load(f)

STOP_WORDS_SET = set(stopwords.words('english'))
UNDESIRED_WORDS_SET = FOOD_MEASUREMENTS_SET.union(DESCRIPTION_WORDS_SET.union(STOP_WORDS_SET))
numbers_finder = r"\d[A-Za-z]*"
extra_info_finder = r"[(][A-Za-z0-9]*"
extra_info_finder_2 = r"[A-Za-z0-9]*[)]"
filtered_ingredients = list()
ingredients = data['ingredientLines']
print(ingredients)
for x in ingredients:
    result = list()
    words_list = x.split()
    for index, y in enumerate(words_list):
        if bool(re.match(extra_info_finder, y)):
            index2 = [i for i, item in enumerate(words_list[index:]) if re.search(extra_info_finder_2, item)][0]
            del words_list[index:index + index2 + 1]
            continue
        parsed_y = parse(y)['name']
        parsed_y = parsed_y.strip(punctuation).lower()
        if not bool(re.match(numbers_finder, parsed_y)) and parsed_y not in UNDESIRED_WORDS_SET:
            result.append(parsed_y)
    filtered_ingredients.append(" ".join(result))

print(filtered_ingredients)
