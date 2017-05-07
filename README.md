U ovoj zadaći korišten je mapFragment koji očitava sam lokaciju korisnika, a moguće je i klikom na mapu očitati određene koorinate neke točke na Zemlji. U TextView iznat mapFragmenta ispisuju se geografska širina i dužina, mjesto, adresa i država. Iznad TextViewa postoji gumb za otvaranje kamere preko implicitnog intenta. Kada je završeno slikanje pomoću notificationManagera pozvano je ispisivanje notifikacije sa svim njenim atributima. Klikom na notifikaciju otvara se spremljena slika. To se izvršilo tako da smo predali putanju do slike koju smo pronašli preko Uri-a. Najviše problema bilo je oko tog spremanja slike i oko predavanja lokacije. Najviše od literature su se koristili materijali s laboratorijskih vježbi. Imala sam problema s repozitorijem pa sam morala sve prebaciti u novi, zato ima tako malo commitova.

Literatura:

Materijali s laboratorijskih vježbi
1. http://stackoverflow.com/questions/19144723/how-to-add-sound-to-an-onclick-event-in-an-android-application
2. http://stackoverflow.com/questions/17379807/remove-previous-marker-and-add-new-marker-in-google-map-v2
3. http://stackoverflow.com/questions/12995185/android-taking-photos-and-saving-them-with-a-custom-name-to-a-custom-destinati
4. https://www.google.hr/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjplZOrz97TAhVG0hoKHUHtAuoQjRwIBw&url=http%3A%2F%2Fwww.iconarchive.com%2Ftag%2Fmap&psig=AFQjCNHjNEfLhVhg5kK8ByHTbINilAlFLw&ust=1494275274734272 
5. http://stackoverflow.com/questions/31099140/how-to-convert-lat-lng-to-a-location-variable
