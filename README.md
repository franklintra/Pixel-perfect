# QOI image format encoder and decoder
# Made during the EPFL computer science course

## Warning to read!

Les tests du projet situés dans main.java ne s'executeront pas si le dossier tests situé à la racine du projet n'est pas présent.
Dans le dossier tests, il faut des images en png ainsi qu'un sous-dossier generated situé à la racine du dossier tests.
Des images de tests ainsi que le dossier sont fournis par mail à l'adresse suivante: hamza.remmal@epfl.ch comme convenu avec lui
Ainsi que sur le lien suivant: https://drive.google.com/file/d/1OKP50d4h4K7aquAAqOwNROpEN3cgtxGb/view?usp=share_link

#### Ou placer les tests?
```
src/
├─ cs107/
tests/
├─ generated/
├─ image1.png
├─ test2.png
├─ ...

```

### Fonctions rajoutées pour rendre le code plus lisible dans ArrayUtils:
###
1. #### ARGBtoRGBA
* ##### Paramètres: Deux byte[][]
* ##### Utilisation: Permet de changer l'ordre des channels dans une liste de byte[] pour passer de ARGB à RGBA.
* ##### Exemple: ARGBtoRGBA([0, 14, 255, 257]) = [14, 255, 257, 0]
* ##### Return: Un byte[][]
###
2. #### RGBAtoARGB
* ##### Paramètres: Deux byte[][]
* ##### Utilisation: Permet de changer l'ordre des channels dans une liste de byte[] pour passer de RGBA à ARGB.
* ##### Exemple: RGBAtoARGB([14, 255, 257, 0]) = [0, 14, 255, 257]
* ##### Return: Un byte[][]
###
3. #### calculateDelta
* ##### Paramètres: Deux byte[]
* ##### Utilisation: Permet de calculer la différence entre deux byte[] de même taille.
* ##### Exemple: calculateDelta([0, 15, 256, 258], [0, 14, 255, 257]) = [0, 1, 1, 1]
* ##### Return: Un byte[]
###
4. #### endsWith
* ##### Paramètres: Deux byte[]
* ##### Utilisation: Permet de vérifier si un byte[] se termine par un autre byte[].
* ##### Exemple1 : endsWith([1,2,3,4,5,6,7,8,9,10], [7,8,9,10]) retourne true
* ##### Exemple2 : endsWith([1,2,3,4,5,6,7,8,9,10], [[7,8,9,10]]) retourne false
* ##### Return: Un boolean
##
### Fonctions de test:
###
1. #### testEncodeImage
* ##### Paramètres: String[] img_name_list dans le dossier res/
* ##### Utilisation: Cette fonction permet d'encoder toutes les images de res/img_path[i].png dans le format QOI et de comparer les images obtenues avec l'image en QOI correspondant à celle en PNG donnée par l'énoncé.
* ##### Return: Un boolean
###
2. #### testDecodeImage
* ##### Paramètres: String[] img_name_list dans le dossier res/
* ##### Utilisation: Cette fonction permet de décoder toutes les images de res/img_path[i].qoi en PNG et de comparer les images obtenues avec l'image en QOI correspondant à celle en PNG donnée par l'énoncé.
###
4. #### testEncodeDecode
* ##### Paramètres: String img_name
* ##### Utilisation: Cette fonction permet d'encoder (une image dans le format QOI à partir d'une image PNG), puis de la décoder (une image PNG à partir d'une image QOI) et de comparer avec l'image d'origine. (Les nouveaux png et qoi sont créés dans tests/generated/)
* ##### Return: Un boolean
###
5. #### testEncodeDecodeTests
* ##### Paramètres: Aucun
* ##### Utilisation: Cette fonction encode une image dans le format QOI à partir d'un PNG. Puis encode un PNG à partir du QOI encodé juste avant. Pour créer ces deux fichiers, elle fait appel à la méthode ci-dessus, testEncodeDecode. Et finalement compare chaque pixel du PNG d'origine avec celui créé.
* ##### Return: Un boolean

### Méthodes annexes utilisées pour les tests:
1. #### listTests
* ##### Paramètres: aucune
* ##### Utilisation: Cette fonction permet de lister tous les png dans le dossier tests/ et les renvoie sans l'extension.
* ##### Return: String[tests/*.png] sans l'extension
###
2. #### writeTests
* ##### Paramètres: String img_name, byte[] img
* ##### Utilisation: Cette méthode permet d'écrire un fichier à partir de son contenu binaire dans le dossier tests/generated/img_name.
###
4. #### writeImageTests
* ##### Paramètres: BufferedImage img, String name
* ##### Utilisation: Cette méthode permet d'écrire un fichier en png à partir de son type image dans le dossier tests/generated/img_name.
