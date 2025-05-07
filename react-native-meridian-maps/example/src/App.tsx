import { Text, View, StyleSheet, SafeAreaView, Button, Alert } from 'react-native';
// Import both the module and component
import { MeridianMapView, MeridianMaps } from 'react-native-meridian-maps';
import { useState } from 'react';

export default function App() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleOpenMap = async () => {
    try {
      setLoading(true);
      setError(null);
      // Pass undefined values for appId and mapId to use the default values
      const result = await MeridianMaps.openMap(undefined, undefined);
      console.log('Map opened:', result);
    } catch (err: any) {
      console.error('Error opening map:', err);
      setError(err?.message || 'Unknown error');
      Alert.alert('Error Opening Map', err?.message || 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenTest = async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await MeridianMaps.openTestActivity();
      console.log('Test activity opened:', result);
    } catch (err: any) {
      console.error('Error opening test activity:', err);
      setError(err?.message || 'Unknown error');
      Alert.alert('Error Opening Test', err?.message || 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>Meridian Maps Diagnostic</Text>
        
        <View style={styles.buttonContainer}>
          <Button 
            title="Open Map" 
            onPress={handleOpenMap} 
            disabled={loading} 
          />
          
          <View style={styles.buttonSpacer} />
          
          <Button 
            title="Run Diagnostic Test" 
            onPress={handleOpenTest} 
            disabled={loading}
            color="#4CAF50"
          />
        </View>
        
        {loading && <Text style={styles.status}>Loading...</Text>}
        {error && <Text style={styles.error}>{error}</Text>}
        
        <View style={styles.mapContainer}>
          <Text style={styles.mapLabel}>MapView Component:</Text>
          <MeridianMapView 
            appId="5809862863224832" 
            mapId="5668600916475904" 
            style={{ 
              width: '100%', 
              height: 500, // Make the map much larger for visibility
              borderWidth: 5,
              borderColor: 'red',
              backgroundColor: '#FFDDDD'
            }}
          />
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    color: '#333',
    textAlign: 'center',
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 20,
  },
  buttonSpacer: {
    width: 12,
  },
  status: {
    marginVertical: 10,
    fontSize: 16,
    textAlign: 'center',
    color: '#666',
  },
  error: {
    marginVertical: 10,
    padding: 10,
    fontSize: 14,
    textAlign: 'center',
    color: '#D32F2F',
    backgroundColor: '#FFEBEE',
    borderRadius: 4,
  },
  mapLabel: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#555',
  },
  mapContainer: {
    flex: 1,
    width: '100%',
    minHeight: 300, // Ensure minimum height
    borderWidth: 2,
    borderColor: '#E91E63', // Make border more visible for debugging
    borderRadius: 8,
    overflow: 'hidden',
    backgroundColor: '#fff',
    padding: 0, // Remove padding to maximize map size
    marginTop: 10,
  },
});
