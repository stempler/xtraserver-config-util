package de.interactive_instruments.xtraserver.config.util;

import java.io.*;
import java.util.*;

/**
 * @author zahnen
 */
public class SubstitutionProcessor {

    // Data

    // Substitution map
    Map<String, String> m_substmap;
    // Basic input object, unread buffer, savepoint buffer
    //Input* m_pIn;
    Reader in;
    StringBuffer m_unread;
    StringBuffer m_saved;
    int m_nSaved;
    // Processed output, unread buffer
    StringBuffer m_unread_prd;

    Set<String> m_missingKeys;

    // process xincludes
    boolean m_processXincludes;
    File m_xincludeConfigDir;
    // Xinclude map
    Map<String,File> m_xincludemap;

    public SubstitutionProcessor() {
        this.m_substmap =new HashMap<>();
        this.m_missingKeys = new HashSet<>();
        this.m_xincludemap = new HashMap<>();

        this.m_unread = new StringBuffer();
        this.m_saved = new StringBuffer();
        this.m_unread_prd = new StringBuffer();
    }

    public void addParameter(String key, String value) {
        //TODO: EncodeXMLEntities
        m_substmap.put(key, value);
    }

    public void addParametersFromProperties() {

    }

    public void enableXIncludes(File baseDir) {
        this.m_xincludeConfigDir = baseDir;
        this.m_processXincludes = true;
    }

    public void process(File input, Writer output) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input),"UTF-8"));

        process(reader, output);
    }

    public void process(Reader input, Writer output) throws IOException {
        String config = "";
        int ich;

        while ((ich = getProcessedChar(input)) != -1) {
            output.write(ich);
        }

        input.close();
        output.close();

        if (m_processXincludes) {
            for (Map.Entry<String,File> include: m_xincludemap.entrySet()) {
                Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(m_xincludeConfigDir, include.getKey())),"UTF-8"));
                Writer writer = new OutputStreamWriter(new FileOutputStream(include.getValue()));

                process(reader, writer);
            }
        }
    }

    private int getProcessedChar(Reader input) throws IOException {
        int ich;

        // Wenn was im unread-Puffer steht, dieses abholen, ansonsten
        // vom Input lesen
        if( m_unread_prd.length() > 0)
        {
            // Vom unread-Puffer
            ich = m_unread_prd.charAt(m_unread_prd.length()-1);
            m_unread_prd.setLength(m_unread_prd.length()-1);
        }
        else
            // Nichts da. Weiter im Input ...
            ich = readByte(input);

        // EOD muss nicht weiter verarbeitet werden
        if( ich == -1 )
            return ich;

        // Liegt möglicherweise ein Einsetzpunkt vor?
        if( ich == '{' )
        {
            // Das nächste Zeichen inspizieren
            int jch, kch;
            if( m_unread_prd.length() > 1 )
            {
                jch = m_unread_prd.charAt(m_unread_prd.length()-1);
                kch = m_unread_prd.charAt(m_unread_prd.length()-1);
            }
            else if( m_unread_prd.length() > 0 )
            {
                jch = m_unread_prd.charAt(m_unread_prd.length()-1);
                kch = readByte(input);
                unreadByte( kch );
            }
            else
            {
                jch = readByte(input);
                kch = readByte(input);
                unreadByte( kch );
                unreadByte( jch );
            }

            // Verzweigen je nach nächsten Zeichen
            if( jch == -1 )
            {
                // EOD nach geschweifter Klammer
                return '{';
            }
            if( jch == '$' )
            {
                // $: Muss Substitution sein
                ich = doSubstitution(input);
            }
            else if( jch == 'f' )
            {
                // f: sollte ein {forall ...}...{end} sein
                ich = doForAll(input);
            }
            else if( jch == 'i' )
            {
                // in: sollte ein {intersects s1 s2 true false} sein
                //if ( kch == 'n' )
                //    ich = doIntersects(input);
                // if: sollte ein {if s1 s2}...{else}...{fi} sein
                if ( kch == 'f' )
                    ich = doIf(input);
            }
        }
        else if( m_processXincludes && ich == '<' )
        {
            // Die nächsten beiden Zeichen inspizieren
            int jch, kch;
            if( m_unread_prd.length() > 1 )
            {
                jch = m_unread_prd.charAt(m_unread_prd.length()-1);
                kch = m_unread_prd.charAt(m_unread_prd.length()-1);
            }
            else if( m_unread_prd.length() > 0 )
            {
                jch = m_unread_prd.charAt(m_unread_prd.length()-1);
                kch = readByte(input);
                unreadByte( kch );
            }
            else
            {
                jch = readByte(input);
                kch = readByte(input);
                unreadByte( kch );
                unreadByte( jch );
            }

            // Verzweigen je nach nächsten Zeichen
            if( jch == 'x' && kch == 'i' )
            {
                // xi: sollte xinclude sein
                ich = doXinclude(input);
            }
        }
        return ich;
    }


    // Ein Zeichen lesen
    private int readByte(Reader input) throws IOException {
        // Input besorgen
        int ich;
        if( m_unread.length() > 0 )
        {
            // Aus dem unread-Stack
            ich = m_unread.charAt(m_unread.length()-1);
            m_unread.setLength(m_unread.length()-1);
        }
        else
        {
            // Echter Input
            ich = input.read();
        }

        // Falls es Savepoints gibt, aufzeichnen
        if( m_nSaved > 0 )
            m_saved.append(ich);

        // Das Zeichen abliefern
        return ich;
    }

    // Ein Zeichen auf den Input zurück legen
    private void unreadByte( int ich )
    {
        // Auf den unread-Stack
        m_unread.append( ich );

        // Aus den Savepoints entnehmen
        if( m_nSaved > 0 ) {
            if (m_saved.length() > 0) {
                m_saved.setLength(m_saved.length() - 1);
            }
        }
    }

    // Savepoint setzen und identifizieren
    private int takeSavepoint()
    {
        // Savepoint ist der Füllstand des Savepoint-Feldes. Den Savepoint-
        // Zähler erhöhen. Die Inputfunktion zeichnet automatisch im
        // Savepoint-Feld auf, wenn der Zähler > 0 ist.
        m_nSaved++;
        return m_saved.length();
    }

    // Savepoint aufgeben
    private void releaseSavepoint( int isp )
    {
        // Die Eingabezeichen bis zum Index freigeben. Nur beim letzten Mal
        // wird der Puffer leer geräumt

        if( --m_nSaved <= 0 )
            m_saved.setLength( 0 );
    }

    // Auf Savepoint zurücksetzen
    private void restoreSavepoint( int isp )
    {
        // Die geretteten Eingabezeichen bis zum gegebenen Index werden in die
        // unread-Schnittstelle gesteckt und stehen daher als Input wieder zur
        // Verfügung.
        for( int i = m_saved.length()-1; i >= isp; i-- )
        {
            int ich = m_saved.charAt(m_saved.length()-1);
            m_saved.setLength(m_saved.length() - 1);
            m_unread.append( ich );
        }
    }

    // Substitution durchführen. Erkannt ist {$, wobei der $ das nächste
// Zeichen ist.
    private int doSubstitution(Reader input) throws IOException {
        // Für den Fall, dass die Substitution nicht glückt, setzen wir einen
        // Savepoint
        int savepoint = takeSavepoint();

        // Den $ wegnehmen
        int ich = getProcessedChar(input);

        // Den Key bis zur } oder EOF lesen. Wir lesen aus dem substituierten Ergebnis,
        // sodass rekursive Ersetzung geht: {${$abc}}
        StringBuilder key = new StringBuilder();
        while( (ich = getProcessedChar(input)) != -1 )
        {
            if( ich=='}' ) break;
            key.append(ich);
        }

        // Falls wir EOD erreicht haben, dies auf alle Fälle zurück
        if( ich == -1 )
            unreadByte( ich );

        // Mit dem Key suchen
        String value = m_substmap.get(key.toString());
        if( value != null )
        {
            // Gefunden: Der Wert ersetzt das {$key} Konstrukt. Das kommt alles
            // in den unread-Puffer für prozessierte Texte
            for( int i = value.length()-1; i > 0; --i )
                m_unread_prd.append(value.charAt(i));

            if (value.length() > 1 && value.charAt(0) == '{' && value.charAt(1) == '$')
                ich = doSubstitution(input);
            else if(value.length() > 2 && value.charAt(0) == '<' && value.charAt(1) == 'x' && value.charAt(2) == 'i' )
                ich = doXinclude(input);
            else if( value.length() > 0 )
                ich = value.charAt(0);
            else
                // Diese Variable stellt den leeren String dar; wir müssen den nächsten
                // Processed Char zurückbringen:
                ich = getProcessedChar(input);

            // Erfolg: Savepoint auflösen
            releaseSavepoint( savepoint );
        }
        else
        {
            // Nicht gefunden: der Substitutionspunkt wird durch Leerstring ersetzt.
            // Der Key wird vermerkt, damit er später geloggt werden kann.
            m_missingKeys.add(key.toString());

            // Diese Variable stellt den leeren String dar; wir müssen den nächsten
            // Processed Char zurückbringen:
            ich = getProcessedChar(input);

            // Savepoint auflösen
            releaseSavepoint( savepoint );
        }
        return ich;
    }

    // {forall x list} Schleife durchführen. Erkannt wurde bereits die geschweifte
// Klammer am Anfang.
    private int doForAll(Reader input) throws IOException {
        StringBuffer unread = new StringBuffer();

        // Erstmal das forall-Operationswort erkennen
        String forall = "forall ";
        int ich, i;
        for( i=0; i < forall.length(); i++ )
        {
            ich = readByte(input);
            unread.append( ich );
            if( ich != forall.charAt(i) ) break;
        }

        // Wenn es gut gegangen ist, erkennen wir jetzt den Key und den Wert bis
        // zum }. Key und Wert werden aus dem substituierten Text geholt.
        if( i == forall.length() )
        {
            // Hole Key und Value des {forall key value}
            String[] args = new String[2];
            int iarg = 0;
            while( (ich = getProcessedChar(input)) != -1 )
            {
                if( ich==' ' )
                    if( iarg==0 )
                    {
                        if ( args[0].length() > 0 ) iarg = 1;
                        continue;
                    }
                if( ich == '}' ) break;
                args[iarg] += ich;
            }

            // Falls wir EOD erreicht haben, dies auf alle Fälle zurück
            if( ich == -1 )
                m_unread_prd.append(ich);

            // { forall ...}  ist erkannt, einschließlich der schließenden Klammer.
            // Das zweite Argument ist als Liste zu interpretieren und auseinander
            // zu nehmen. Das erste ist der Key.
            String key = args[0];
            String valueList = args[1];
            char dlm = ' ';
            if( valueList.length() > 0 )
            {
                dlm = valueList.charAt(0);
                valueList = valueList.substring( 0, valueList.lastIndexOf(dlm) + 1 );
            }
            List<String> values = new ArrayList<>();
            for( i = 0; i < valueList.length(); i++ )
            {
                if( valueList.charAt(i) == dlm )
                    values.add( "" );
                else
                    values.set(values.size()-1, values.get(values.size()-1) + valueList.charAt(i));
            }
            if( values.size() > 0 && values.get(values.size()-1).length() == 0 )
                values.remove( values.size()-1 );

            // Den Zustand der Substitutionstabelle bezüglich des Keys festhalten
            String value = m_substmap.get( key );
            boolean keyWasSet = value != null;

            // Den Inputzustand als Savepoint festhalten
            int savepoint = takeSavepoint();

            // Die Ergebnisse festhalten
            StringBuffer record = new StringBuffer();

            // Über die Werteliste laufen und jeden Wert mit seinem Key etablieren
            boolean isEmpty = false;
            if( values.size()==0 )
            {
                isEmpty = true;
                values.add( "" );
            }
            for( int il = 0; il < values.size(); il++ )
            {
                // Key-Werte-Paar eintragen
                m_substmap.put(key, values.get(il));

                // Zurück auf den Savepoint
                restoreSavepoint( savepoint );

                // Jetzt den verarbeiteten Input lesen, bis EOF oder {end} auftaucht
                while( (ich = getProcessedChar(input)) != -1 )
                {
                    record.append( ich );
                    if( record.toString().endsWith("{end}") ) {
                        record.setLength(record.length() - 5);
                        break;
                    }
                }
            }
            if( isEmpty ) record.setLength(0);

            // Den Savepoint abschaffen: qqq{forall x /{$xyz}/xxx/yyy/}-{$x}+{end}rrr
            releaseSavepoint( savepoint );

            // Den alten Zustand der Substitutionstabelle wiederherstellen
            if( keyWasSet )
                m_substmap.put(key, value);
            else
                m_substmap.remove( key );

            // Alles, was wir gesammelt haben, in den prozessierten Input zurück
            for( i = record.length()-1; i > 0; --i )
                m_unread_prd.append( record.charAt(i) );

            // Das erste Zeichen zurück
            if( record.length() > 0 )
                return record.charAt(0);
            else
                // Diese Anweisung stellt den leeren String dar; wir müssen den nächsten
                // Processed Char zurückbringen:
                return getProcessedChar(input);
        }


        // Fehlerbehandlung: Alles, was wir nicht verstanden haben, einschließlich
        // der {, muss zurück
        while( unread.length() > 0 )
        {
            ich = unread.charAt(unread.length()-1);
            unread.setLength(unread.length() - 1);
            unreadByte( ich );
        }
        unreadByte( '{' );
        return readByte(input);
    }

    // {if value1 == value2}...{else}...{fi} bzw. {if value1 != value2}...{else}...{fi}
// durchführen und den nächsten Processed Char dieser Anweisung zurückbringen.
// Fals diese Anweisung den leeren String darstellt, bringen wir den nächsten
// Processed Char *hinter* dieser Anweisung zurück.
// Erkannt wurde bereits die geschweifte Klammer am Anfang.
// Wir ignorieren alle Zeichen zwischen {if und dem nächsten Whitespace.
// Innerhalb der if-Klammer werden alle Whitespaces zwischen if, Argumenten und ==
// bzw. != ignoriert. Whitespaces in Argumenten werden aber berücksichtigt.
// Beachte: Argumente dürfen kein == oder != enthalten
    static String whites = "\r\n\t ";

    // Unterscheiden: {if value1},  {if value1 == value2} oder {if value1 != value2}
    enum CompType { none, equal, notEqual };

    private int doIf(Reader input) throws IOException {
        int ich, i;
        // Wenn es gut gegangen ist, erkennen wir jetzt den Key und den Wert bis
        // zum }. Key und Wert werden aus dem substituierten Text geholt.
        if( readOperation(input, "if") )
        {
            // Hole Values
            String[] args = new String[2];
            int iarg = -1; // Index bzgl. args
            CompType compType = CompType.none;

            while( (ich=getProcessedChar(input)) != -1 )
            {
                // Beim ersten Whitespace schalten wir auf das erste Argument
                // um, d.h. bis zum ersten Whitespace ignorieren wir alle Zeichen.
                if( iarg == -1 && whites.indexOf(ich) > -1 )
                {
                    iarg = 0;
                    continue;
                }

                if( iarg==0 )
                {
                    // Erstes Argument
                    // Wir unterscheiden == und != am Ende des Arguments
                    if( args[0].length() >= 2 && args[0].charAt(args[0].length()-1) == '=' )
                    {
                        if( args[0].charAt(args[0].length()-2) == '=' )
                            compType = CompType.equal;
                        else if( args[0].charAt(args[0].length()-2) == '!' )
                            compType = CompType.notEqual;
                    }

                    if( compType != CompType.none )
                    {
                        // Vergleichs-Symbol gefunden: weiter mit zweitem Argument
                        iarg = 1;
                        // Vergleichssymbol löschen
                        args[0] = args[0].substring(0, args[0].length()-2);
                    }
                }

                // Ende der if-Klammer
                if( ich=='}' ) break;

                // Argument lesen
                if (iarg > -1)
                    args[iarg] += ich;
            }

            // Falls wir EOD erreicht haben, dies auf alle Fälle zurück
            if( ich == -1 )
                m_unread_prd.append( ich );

            // Die Ergebnisse festhalten
            StringBuffer recordIf = new StringBuffer();
            StringBuffer recordElse = new StringBuffer();
            boolean isElse = false;

            // Jetzt den verarbeiteten Input lesen, bis EOF oder {fi} auftaucht
            while( (ich=getProcessedChar(input)) != -1 )
            {
                if (!isElse)
                {
                    recordIf.append( ich );
                    if( recordIf.toString().endsWith("{fi}") ) {
                        recordIf.setLength( recordIf.length() - 4 );
                        break;
                    }
                    else if( recordIf.toString().endsWith("{else}") ) {
                        recordIf.setLength( recordIf.length() - 6 );
                        isElse = true;
                    }
                }
                else
                {
                    recordElse.append( ich );
                    if( recordElse.toString().endsWith("{fi}") ) {
                        recordElse.setLength( recordElse.length() - 4 );
                        break;
                    }
                }
            }


            // Wir ignorieren Whitespaces an den Argumenten.
            args[0] = args[0].trim();
            args[1] = args[1].trim();
            // Falls wir nur einen Operanden haben, unterstützen wir auch ein
            // not-exists: {if !value1}.
            boolean notExists = false;
            if( iarg == 0 && args[0].length() > 0 && args[0].charAt(0) == '!' )
            {
                notExists = true;
                args[0] = args[0].substring(1).trim();
            }


            // Den if-Ausdruck auswerten
            // Wir nehmen den if-Block wenn
            // - es zwei Operanden gibt und diese bei einem Stringvergleich identisch bzw. verschieden sind
            // - es nur einen (evtl. negierten) Operanden gibt und dieser weder nicht gesetzt, leer, "false" oder "0" ist
            if( (args[1].length() > 0 && ((compType == CompType.equal) ? args[0].equals(args[1]) : !args[0].equals(args[1]))) ||
                    (args[1].length() == 0 && !notExists && args[0].length() > 0 && !args[0].equals("false") && !args[0].equals("0")) ||
                    (args[1].length() == 0 && notExists && (args[0].length() == 0 || args[0].equals("false") && args[0].equals("0"))) )
            {
                // if-Zweig

                // Alles, was wir gesammelt haben, in den prozessierten Input zurück
                for( i =recordIf.length()-1; i > 0; --i )
                    m_unread_prd.append( recordIf.charAt(i) );

                // Das erste Zeichen zurück
                if( recordIf.length() > 0 )
                    return recordIf.charAt(0);
                else
                    // Diese if-Anweisung stellt den leeren String dar; wir müssen den nächsten
                    // Processed Char zurückbringen:
                    return getProcessedChar(input);
            }
            else
            {
                // else-Zweig

                // Alles, was wir gesammelt haben in den prozessierten Input zurück
                for( i= recordElse.length()-1; i > 0; --i )
                    m_unread_prd.append( recordElse.charAt(i) );

                // Das erste Zeichen zurück
                if( recordElse.length() > 0 )
                    return recordElse.charAt(0);
                else
                    // Diese if-Anweisung stellt den leeren String dar; wir müssen den nächsten
                    // Processed Char zurückbringen:
                    return getProcessedChar(input);
            }
        }

        // {if konnte nicht gelesen werden, das letzte gelesene Zeichen war das
        // { davon.
        return '{';
    }

    // Substitution durchführen. Erkannt ist <xi, wobei < das nächste
// Zeichen ist.
    private int doXinclude(Reader input) throws IOException {
        // Für den Fall, dass die Substitution nicht glückt, setzen wir einen
        // Savepoint
        int savepoint = takeSavepoint();

        StringBuilder buffer = new StringBuilder("<");
        int ich;
        int i = 0;
        String xi = "xi:include";
        while( (ich=getProcessedChar(input)) != -1 )
        {
            buffer.append(ich);
            if( ich != xi.charAt(i++) )
            {
                restoreSavepoint(savepoint);
                return '<';
            }
            if( i == 10 ) break;
        }

        // xi:xinclude bis zum Ende des href oder > oder EOF lesen.
        StringBuilder file = new StringBuilder();
        while( (ich=getProcessedChar(input)) != -1 )
        {
            if (buffer.length() >= 6 && buffer.toString().substring(buffer.length()-6,6).equals("href=\""))
            {
                if (ich == '"')
                {
                    //unreadByte( ich );
                    break;
                }
                file.append(ich);

            }
            else
                buffer.append(ich);
            if( ich=='>' )
            {
                restoreSavepoint(savepoint);
                return '<';
            }
        }

        // Falls wir EOD erreicht haben, dies auf alle Fälle zurück
        if( ich == -1 )
        {
            restoreSavepoint(savepoint);
            return '<';
        }

        // tmpfile bestimmen
        File tmpFile = m_xincludemap.get(file.toString());
        if (tmpFile == null)
        {
            tmpFile = File.createTempFile("xtraserver_", "_tmp");
            tmpFile.deleteOnExit();
            m_xincludemap.put(file.toString(), tmpFile);
        }
        String xmlBase = "\" xml:base=\"" + tmpFile.getParent().replaceAll("\\\\", "/") + "\"";
        String fileName = tmpFile.getName();

        m_unread_prd.append(xmlBase);
        m_unread_prd.append(fileName);
        m_unread_prd.append(buffer);

        ich = '<';

        // Erfolg: Savepoint auflösen
        releaseSavepoint( savepoint );

        return ich;
    }

    private boolean readOperation(Reader input, String op) throws IOException {
        StringBuffer unread = new StringBuffer();
        // Erstmal das Operationswort erkennen
        int ich, i;
        for( i=0; i < op.length(); i++ )
        {
            ich = readByte(input);
            unread.append( ich );
            if( ich != op.charAt(i) ) break;
        }

        // Wenn es gut gegangen ist
        if( i == op.length() )
            return true;

        // Fehlerbehandlung: Alles, was wir nicht verstanden haben, einschließlich
        // der {, muss zurück
        while( unread.length() > 0 )
        {
            ich = unread.charAt(unread.length()-1);
            unread.setLength(unread.length() - 1);
            unreadByte( ich );
        }

        return false;
    }


    private void cleanup() {
        if (m_processXincludes) {
            for (File tmpFile: m_xincludemap.values()) {
                tmpFile.delete();
            }
        }
    }
}









/*

// {intersects list1 list2 true false} auswerten. Erkannt wurde bereits
// die geschweifte Klammer am Anfang.
        int SubstProcessor::doIntersects()
        {
        std::vector<int> unread;

        // Erstmal das intersects-Operationswort erkennen
        const char intersects[] = "intersects ";
        int ich, i;
        for( i=0; i<(int)strlen(intersects); i++ )
        {
        ich = readByte();
        unread.push_back( ich );
        if( ich!=intersects[i] ) break;
        }

        // Wenn es gut gegangen ist, erkennen wir jetzt die zwei bis vier
        // Argumente.
        if( i==strlen(intersects) )
        {
        // Hole die bis zu vier Argumente
        std::string args[4];
        int iarg = 0;
        while( (ich=getProcessedChar())!=EOF )
        {
        if( ich==' ' )
        if( iarg<3 )
        {
        if ( args[iarg].length()>0 ) iarg++;
        continue;
        }
        if( ich=='}' ) break;
        args[iarg] += ich;
        }

        // Falls wir EOD erreicht haben, dies auf alle Fälle zurück
        if( ich==EOF )
        m_unread_prd.push_back( ich );

        // Wir ignorieren Whitespaces an den Value-Argumenten (d.h. arg3 und 4).
        args[2] = XSHelpers::trim(args[2]);
        args[3] = XSHelpers::trim(args[3]);


        // {intersects ...}  ist erkannt, einschließlich der schließenden Klammer.
        // Für letzten beiden Werte möglicherweise Defaults einsetzen.
        if( ! args[2].length() ) args[2] = "true";
        if( ! args[3].length() ) args[3] = "false";

        // Die ersten beiden sind Listen, die wir zerlegen und als Mengen
        // ablegen
        std::set<std::string> sets[2];
        for( int j=0; j<2; j++ )
        {
        char dlm = ' ';
        if( args[j].length()>0 ) dlm = args[j][0];
        std::string val;
        for( i=0; i<(int)(args[j].length()); i++ )
        {
        if( args[j][i]==dlm )
        {
        if( val.length()>0 ) sets[j].insert( val );
        val = "";
        }
        else
        val += args[j][i];
        }
        }

        // Herausfinden, ob die beiden Mengen einen nicht-leeren Schnitt haben
        bool found = false;
        std::set<std::string>::iterator it = sets[0].begin();
        while( it!=sets[0].end() )
        {
        if( sets[1].find( *it++ ) != sets[1].end() )
        {
        found = true; break;
        }
        }

        // Das je nach Ergebnis ersetzt eins der Ergebnisargumente den gesamten
        // Ausdruck
        std::string value = found ? args[2] : args[3];
        for( int i=(int)(value.length())-1; i>0; --i )
        m_unread_prd.push_back( (unsigned char)(value[i]) );
        if( value.size()>0 )
        ich = value[0];
        else
        // Diese Anweisung stellt den leeren String dar; wir müssen den nächsten
        // Processed Char zurückbringen. (Offenbar ist es durch die Defaultierung
        // z.Zt. gar nicht möglich, dass hier der leere String zurückkommt.)
        ich = getProcessedChar();

        return ich;
        }

        // Fehlerbehandlung: Alles, was wir nicht verstanden haben, einschließlich
        // der {, muss zurück
        while( unread.size()>0 )
        {
        ich = unread.back();
        unread.pop_back();
        unreadByte( ich );
        }
        unreadByte( '{' );
        return readByte();
        }


*/





