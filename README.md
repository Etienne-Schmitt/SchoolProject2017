# SchoolProject2017


Ce serveur utilise 2 thread :

Recéption
Émission

Ces deux ont chancun leur fonction propre :

Récéption : Est chargé de stocké les données recu 
Émission  : Est chargé d'envoyer les données en bluetooth






Attention !!

Il est obligatoire de configurer votre daemon SDP pour qu'il puisse proposer la connection en port série.

Pour ce faire il faut vérifier l'existence d'un service Seial Port
Pour afficher cette information il est nécéssaire de disposer des droits admin :


Taper la commande : (sudo) sdptool browse local
une liste non exhaustive s'affiche, et chercher l'existence de cette partie :

Service Name: Serial Port
Service Description: COM Port
Service Provider: BlueZ
Service RecHandle: 0x10005
Service Class ID List:
  "Serial Port" (0x1101)
Protocol Descriptor List:
  "L2CAP" (0x0100)
  "RFCOMM" (0x0003)
    Channel: 1
Language Base Attr List:
  code_ISO639: 0x656e
  encoding:    0x6a
  base_offset: 0x100
Profile Descriptor List:
  "Serial Port" (0x1101)
    Version: 0x0100


Certaines informations (Version, RecHandle peuvent changer !)


Si cette ligne n'éxiste pas il faut la crée avec la commande :
(sudo) sdptool add [--channel=CHANNEL] service 

La partie entre crochet n'est pas obligatoire, sauf si vous voulez utiliser un autre channel que le channel 1 !