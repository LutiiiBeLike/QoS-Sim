# MQTT-QoS-Simulator

Eine interaktive Java-Swing-Anwendung für den Unterricht. Sie macht MQTT Quality of Service (QoS) sichtbar: Ein Paket bewegt sich vom Sender über einen MQTT-Broker zum Empfänger. Der einstellbare Paketverlust zeigt, warum die drei QoS-Stufen unterschiedliche Zuverlässigkeit und unterschiedlichen Aufwand bieten.

> Dies ist eine didaktische Simulation, keine MQTT-Implementierung. Bei echtem MQTT kommuniziert ein Client getrennt mit dem Broker; der Broker kommuniziert anschließend getrennt mit dem Subscriber. Das Diagramm zeigt beide Strecken in einer vereinfachten Ansicht.

## Voraussetzungen und Start

- Ein Standard-JDK ab Java 17 (keine zusätzlichen Bibliotheken)
- Ein Terminal im Projektordner

Kompilieren und starten:

```bash
mkdir -p out
javac --release 17 -d out $(find src/main/java -name '*.java')
java -cp out de.schule.mqttqos.MqttQosSimulator
```

Die Anwendung öffnet ein Fenster. QoS-Stufe und Paketverlust auswählen und anschließend auf **„Nachricht senden“** klicken. Während die Pakete animiert werden, sind die Eingaben gesperrt. Das Ereignisprotokoll beschreibt jede Übertragung und das Ergebnis.

## MQTT-QoS kurz erklärt

| Stufe | Zusage | Ablauf in dieser Simulation |
| --- | --- | --- |
| QoS 0 | Höchstens einmal | `PUBLISH` ohne Bestätigung. Ein verlorenes Paket wird nicht erneut gesendet. |
| QoS 1 | Mindestens einmal | `PUBLISH` und `PUBACK`. Geht das `PUBACK` verloren, wird `PUBLISH` mit DUP-Markierung wiederholt; dadurch können Duplikate auftreten. |
| QoS 2 | Genau einmal | `PUBLISH → PUBREC → PUBREL → PUBCOMP`. Der Vier-Schritt-Ablauf verhindert doppelte fachliche Zustellung. |

Der Paketverlust wird bei jeder gerichteten Paketübertragung unabhängig ausgelost. Bei QoS 1 und QoS 2 werden bestätigte Schritte bis zum Erfolg wiederholt. Bei 100 % Verlust beendet die Anwendung die Simulation kontrolliert nach dem ersten verlorenen Paket.

## Ideen für den Workshop

- Zunächst QoS 0, QoS 1 und QoS 2 mit 0 % Verlust vergleichen: Welche Pakete und Bestätigungen kommen hinzu?
- Bei etwa 30 % Verlust mehrfach QoS 1 ausführen und nach der `DUP`-Markierung im Protokoll suchen.
- Bei QoS 2 ein verlorenes `PUBREC`, `PUBREL` oder `PUBCOMP` beobachten und die Wiederholung diskutieren.
- Die Klasse schätzen lassen, welche Stufe für Temperaturdaten, einen Lichtschalter oder eine Bezahlbestätigung sinnvoll wäre. Anschließend Aufwand, Zuverlässigkeit und mögliche Duplikate abwägen.
- 100 % Verlust einstellen und erläutern, warum auch QoS 1/2 keine funktionierende Netzwerkverbindung ersetzen können.
