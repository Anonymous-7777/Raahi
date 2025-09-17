import React, { useEffect, useMemo, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { AlertTriangle, Users, MapPin, Clock } from "lucide-react";
import { collection, onSnapshot } from 'firebase/firestore';
import { db } from '@/lib/firebase';
import LeafletCDNMap from '@/components/LeafletCDNMap';


const Authority = () => {
  const [activeUsers, setActiveUsers] = useState<any[]>([]);
  const [tourists, setTourists] = useState<any[]>([]);
  const parseToken = (token: any, isLat: boolean): number => {
    if (token == null) return NaN;
    const s = String(token).trim();
    const dir = (s.match(/[NnSsEeWw]/)?.[0] || '').toUpperCase();
    const num = s.match(/-?\d+(\.\d+)?/);
    if (!num) return NaN;
    let val = parseFloat(num[0]);
    if (dir === 'S' || dir === 'W') val = -Math.abs(val);
    return val;
  };


  
  const parseCoordString = (str: string) => {
    const cleaned = str.replace(/[\[\]]/g, '').trim();
    const parts = cleaned.split(','); 
    if (parts.length >= 2) {
      const lat = parseToken(parts[0], true);
      const lng = parseToken(parts[1], false);
      if (Number.isFinite(lat) && Number.isFinite(lng)) return { lat, lng };
    }
    return null;
  };
  const parseLocation = (t: any): { lat: number; lng: number } | null => {
    const lat = Number(t?.latitude);
    const lng = Number(t?.longitude);


    if (Number.isFinite(lat) && Number.isFinite(lng)) return { lat, lng };
    const loc = t?.location;
    if (loc && typeof loc === 'object' && Number.isFinite(Number((loc as any).latitude)) && Number.isFinite(Number((loc as any).longitude))) {
      return { lat: Number((loc as any).latitude), lng: Number((loc as any).longitude) };
    }
    if (Array.isArray(loc) && loc.length >= 2)
      {
      const a = parseToken(loc[0], true);
      const b = parseToken(loc[1], false);
      if (Number.isFinite(a) && Number.isFinite(b)) return { lat: a, lng: b };
    }
    if (typeof loc === 'string') 
      {
      const v = parseCoordString(loc);
      if (v) return v;
    }
    return null;


  };

  const escapeHtml = (s: string) =>
    s
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  const buildPopupHtml = (doc: any) => {
    const entries = Object.entries(doc).map(([k, v]) => {
      let val: string;
      try {
        if (k === 'location' && typeof v === 'object' && v && 'latitude' in (v as any) && 'longitude' in (v as any)) 
        {
          val = `(${(v as any).latitude}, ${(v as any).longitude})`;
        } else 
        {
          val = typeof v === 'string' ? v : JSON.stringify(v);
        }
      } catch {
        val = String(v);
      }
      return `<div><strong>${escapeHtml(k)}</strong>: ${escapeHtml(val)}</div>`;
    });


    if (doc.id) {
      entries.unshift(`<div><strong>uid</strong>: ${escapeHtml(String(doc.id))}</div>`);
    }
    return `<div style="min-width:220px"><div style="font-weight:600;margin-bottom:6px">Tourist Details</div>${entries.join('')}</div>`;
  };

  useEffect(() => {
    const unsubUsers = onSnapshot(collection(db, "users"), (snapshot) => {
      const usersData = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
      setActiveUsers(usersData);
    });
    const unsubTourists = onSnapshot(collection(db, "tourist"), (snapshot) => {
      const tData = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
      setTourists(tData);
    });
    return () => {
      unsubUsers();
      unsubTourists();
    };
  }, []);
  const distressTourists = useMemo(
    () => tourists.filter(t => t?.isInDistress === true || String(t?.isInDistress).toLowerCase() === 'true'),
    [tourists]
  );

  const emergenciesCount = distressTourists.length;
  const center = useMemo(() => {
    const def = { lat: 26.1445, lng: 91.7362 };
    for (const t of distressTourists) 
      {
      const c = parseLocation(t);
      if (c) return c;
    }
    for (const t of tourists)
      {
      const c = parseLocation(t);
      if (c) return c;
    }
    return def;
  }, [distressTourists, tourists]);
  const markers = useMemo(() => {
    return distressTourists.map((t) => {
      const coords = parseLocation(t);
      if (!coords) return null;
      return {
        lat: coords.lat,
        lng: coords.lng,
        label: buildPopupHtml(t),
      };
    }).filter(Boolean) as {lat:number;lng:number;label:string}[];
  }, [distressTourists]);

  return (
    <div className="min-h-screen bg-background p-4">
      <div className="container mx-auto max-w-7xl space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-foreground">Authority Dashboard</h1>
          <div className="flex items-center space-x-4">
            <Badge variant="outline" className="text-sm">
              <Users className="w-4 h-4 mr-1" />
              {activeUsers.length} Active Users
            </Badge>
            <Badge variant="destructive" className="text-sm">
              <AlertTriangle className="w-4 h-4 mr-1" />
              {emergenciesCount} Active Emergencies
            </Badge>
          </div>
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <Card className="h-[300px] md:h-[400px] lg:h-[600px]">
              <CardHeader>
                <CardTitle className="flex items-center">
                  <MapPin className="w-5 h-5 mr-2 text-green" />
                  Live Location Map
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="w-full overflow-hidden rounded-md">
                  <LeafletCDNMap
                    center={center}
                    zoom={7}
                    markers={markers}
                    height={480}
                    fitToMarkers={true}
                    sirenOnClick={true}
                    playOnNewMarkers={true}
                  />
                </div>
                <p className="text-xs text-muted-foreground mt-2">Map tiles © OpenStreetMap contributors</p>
              </CardContent>
            </Card>
          </div>



          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center text-destructive">
                  <AlertTriangle className="w-5 h-5 mr-2" />
                  Active Emergencies ({emergenciesCount})
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ScrollArea className="h-48">
                  {emergenciesCount === 0 ? (
                    <p className="text-muted-foreground text-sm">No active emergencies</p>
                  ) : (
                    <div className="space-y-2">
                      {distressTourists.map((t, index) => (
                        <div key={t.id}>
                          <div className="py-2">
                            <p className="text-sm font-medium">{t.name || t.email || t.id}</p>
                            <p className="text-xs text-muted-foreground">{typeof t.location === 'string' ? t.location : `${t.latitude ?? t?.location?.latitude}, ${t.longitude ?? t?.location?.longitude}`}</p>
                          </div>
                          {index < emergenciesCount - 1 && <Separator />}
                        </div>
                      ))}
                    </div>
                  )}
                </ScrollArea>
              </CardContent>
            </Card>




            <Card>
              <CardHeader>
                <CardTitle className="flex items-center text-green">
                  <Users className="w-5 h-5 mr-2" />
                  Active Users ({activeUsers.length})
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ScrollArea className="h-48">
                  <div className="space-y-2">
                    {activeUsers.map((user, index) => (
                      <div key={user.id}>
                        <div className="flex items-center justify-between py-2">
                          <div>
                            <p className="text-sm font-medium">{user.email}</p>
                            <p className="text-xs text-muted-foreground">{user.role}</p>
                          </div>
                        </div>
                        {index < activeUsers.length - 1 && <Separator />}
                      </div>
                    ))}
                  </div>
                </ScrollArea>
              </CardContent>
            </Card>




            <Card>
              <CardHeader>
                <CardTitle className="flex items-center text-muted-foreground">
                  <Clock className="w-5 h-5 mr-2" />
                  Past Emergencies
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ScrollArea className="h-32">
                  <p className="text-muted-foreground text-sm">No past emergencies</p>
                </ScrollArea>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Authority;