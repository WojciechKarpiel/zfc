Obczajam nowe możliwości Javy implementując aksjomaty ZFC

DOZRO: wstawić jakiś system do budowania a nie tak jak bezdomy ręką xD

opcja z Gralem, szybciej startuje

```
echo 'Main-Class: Main' > m.mf 
$JAVA_HOME/bin/jar cvfm Wszystko.jar m.mf  **/*.class
$JAVA_HOME/bin/native-image --no-fallback -cp ./Wszystko.jar -H:Name=prog -H:Class=Main -H:+ReportUnsupportedElementsAtRuntime --link-at-build-time --enable-preview
```

Co można:

1. pisać programy bezpośrednio. Wszystkie puste zbiory są sobie równe (aksjomat pustego zbioru i ekstensjonalności):

    ```
    $./ prog -- << 'EOF'
    (extractWitness
      pustego
      p1 p1P
      (extractWitness
        pustego
        p2 p2P
        (chain
          impliesElo
          (apply (apply ekstensionalności p1) p2)
          (modusPonens
            impliesElo
          
            (forall
              jakiśZbiór
              (and
                (implies
                  (applyConstant (constant elo () (in jakiśZbiór p1)) ())
                  implX
                  (exFalsoQuodlibet
                    (apply p1P jakiśZbiór)
                    implX
                    (applyConstant (constant hehe () (in jakiśZbiór p2)) ())
                    q q
                  )
                )
        
                (implies
                  (applyConstant (constant elo () (in jakiśZbiór p2)) ())
                  implX
                  (exFalsoQuodlibet
                    (apply p2P jakiśZbiór)
                    implX
                    (applyConstant (constant hehe () (in jakiśZbiór p1)) ())
                    q q
                  )
                )
              )
            )
            wynik
            wynik
          )
        )
      )
    )
    EOF
    
    forall
      x
      implies
        forall
          y
          not
            in
              y
              x
        forall
          x_1
          implies
            forall
              y
              not
                in
                  y
                  x_1
            eql
              x
              x_1
    
    ```
2. interaktywnie z taktykami:
    ```
    ./prog
    zapodaj cel
    $ (forall n (implies (forall x (in x n)) (forall x (in x n))))
    No to zaczynamy!
    Uszanowanko! Będziemy rozwiązywać:  (forall n  (implies  (forall x  (in x n )) (forall x  (in x n ))))
    Krok nr 0
    Kontekst:
    Cel na teraz:
    (forall n  (implies  (forall x  (in x n )) (forall x  (in x n ))))
    dawaj:
    $ intro a
    Krok nr 1
    Kontekst:
    CtxElem[name=a, v=n64, tpeNullable=null]
    Cel na teraz:
    (implies  (forall x  (in x n )) (forall x  (in x n )))
    dawaj:
    $ intro b
    Krok nr 2
    Kontekst:
    CtxElem[name=a, v=n64, tpeNullable=null]
    CtxElem[name=b, v=b54, tpeNullable=ForAll[var=x65, f=In[element=x65, set=n64, metadata=[(0,29)-(0,36)]], metadata=[(0,19)-(0,37)]]]
    Cel na teraz:
    (forall x  (in x n ))
    dawaj:
    $ assumption b
    Krok nr 3
    Wszystkie cele spełnione. Generuję rozwiązanie
    No to sprawdzam rozwiązanie:
    IntroForall[v=AstVar[variable=n64, metadata=<brak>], body=IntroImpl[pop=AppliedConstant[fi=Constant[name=?h, freeVariables=[], formula=ForAll[var=x65, f=In[element=x65, set=n64, metadata=[(0,29)-(0,36)]], metadata=[(0,19)-(0,37)]], metadata=<brak>], args=[], metadata=<brak>], v=b54, nast=AstVar[variable=b54, metadata=<brak>], metadata=<brak>], metadata=<brak>]
    OK!
    ```

