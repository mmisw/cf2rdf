2017-06-06

- resolve #3 "upgrade jena library"
  - now using jena 3.3.0
  - various code adjustments as Jena is now more strict.
    In concrete, for the mapping with NVS, there are invalid IRIs in 
    http://vocab.nerc.ac.uk/collection/P07/current/, which Jena now complains about.
    Implemented a workaround simply consisting of replacing the offending spaces with `%20`.
    The offending IRIs are in the following lines:
    
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine dioxide_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_chlorine monoxide_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_dichlorine peroxide_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/mole_fraction_of_hypochlorous acid_in_air"/>
            <owl:sameAs rdf:resource="http://mmisw.org/ont/cf/parameter/rate_of_ hydroxyl_radical_destruction_due_to_reaction_with_nmvoc"/>    
