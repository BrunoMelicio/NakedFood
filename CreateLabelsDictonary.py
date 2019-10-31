l = 3


def create_labels_dictionary(file):
    labels_dictionary = dict()
    with open(file) as f:
        for line_number, label in enumerate(f):
            labels_dictionary['{:0{l}}'.format(line_number, l=l)] = label[:-1]
    return labels_dictionary
