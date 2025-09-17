import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { useNavigate } from "react-router-dom";
import { MapPin, Heart, Users } from "lucide-react";
import heroImage from "@/assets/ne-india-hero.jpg";
import cultureImage from "@/assets/ne-india-culture.jpg";
import attractionsImage from "@/assets/ne-india-attractions.jpg";

const Homepage = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background">
      <section className="relative h-screen flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 z-0">
          <img 
            src={heroImage} 
            alt="North East India landscape"
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-black/60 via-black/40 to-black/60" />
        </div>
        <div className="absolute left-1/2 -translate-x-1/2 top-10 md:top-12 z-20 pointer-events-none">
          <img
            src={new URL("../assets/raahi-logo.png", import.meta.url).href}
            alt="Raahi logo"
            className="h-44 w-44 md:h-60 md:w-60 object-contain select-none"
            draggable={false}
          />
        </div>

        <div className="relative z-10 text-center text-white max-w-4xl mx-auto px-4 mt-24 md:mt-28">
          <h1 className="text-5xl md:text-7xl font-bold mb-6">
            Discover{" "}
            <span className="bg-gradient-to-r from-golden to-green bg-clip-text text-transparent">
              North East India
            </span>
          </h1>
          <p className="text-xl md:text-2xl mb-8 text-gray-200">
            Experience pristine landscapes, vibrant cultures, and warm, safe hospitality of North East India.
               All while traveling with peace of mind, empowered by Raahi's safety-focused platform.
          </p>
          <Button 
            size="lg"
            onClick={() => navigate("/login")}
            className="bg-gradient-to-r from-golden to-green hover:from-golden-dark hover:to-green-dark text-white px-8 py-4 text-lg"
          >
            Start Your Journey
          </Button>
        </div>
      </section>




      <section className="py-20 px-4">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-4xl font-bold mb-6 text-foreground">
                Rich Cultural Heritage
              </h2>
              <p className="text-lg text-muted-foreground mb-6">
                North East India is home to over 200 tribes, each with unique traditions, 
                languages, and customs. From the vibrant festivals of Assam to the ancient 
                monasteries of Arunachal Pradesh, the region offers an unparalleled cultural 
                diversity that has been preserved for centuries.
              </p>
              <div className="flex items-center space-x-4 mb-4">
                <Users className="text-green w-6 h-6" />
                <span className="text-foreground">200+ Indigenous Tribes</span>
              </div>
              <div className="flex items-center space-x-4">
                <Heart className="text-golden w-6 h-6" />
                <span className="text-foreground">Traditional Arts & Crafts</span>
              </div>
            </div>
            <div className="relative">
              <img 
                src={cultureImage} 
                alt="North East India culture"
                className="rounded-lg shadow-lg w-full"
              />
            </div>
          </div>
        </div>
      </section>
      <section className="py-20 px-4 bg-muted">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="relative order-2 md:order-1">
              <img 
                src={attractionsImage} 
                alt="North East India attractions"
                className="rounded-lg shadow-lg w-full"
              />
            </div>



            <div className="order-1 md:order-2">
              <h2 className="text-4xl font-bold mb-6 text-foreground">
                Natural Wonders
              </h2>
              <p className="text-lg text-muted-foreground mb-6">
                From the mighty Brahmaputra River to the snow-capped peaks of the Himalayas, 
                North East India boasts some of the most spectacular natural landscapes in the world. 
                Pristine forests, cascading waterfalls, and unique wildlife sanctuaries await every traveler.
              </p>
              <div className="space-y-4">
                <Card className="border-none shadow-sm">
                  <CardContent className="p-4 flex items-center space-x-3">
                    <MapPin className="text-green w-5 h-5" />
                    <span className="text-foreground">Kaziranga National Park - One-horned Rhinoceros</span>
                  </CardContent>
                </Card>
                <Card className="border-none shadow-sm">
                  <CardContent className="p-4 flex items-center space-x-3">
                    <MapPin className="text-golden w-5 h-5" />
                    <span className="text-foreground">Tawang Monastery - Buddhist Heritage</span>
                  </CardContent>
                </Card>
                <Card className="border-none shadow-sm">
                  <CardContent className="p-4 flex items-center space-x-3">
                    <MapPin className="text-green w-5 h-5" />
                    <span className="text-foreground">Loktak Lake - Floating Islands</span>
                  </CardContent>
                </Card>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>



  );
};
export default Homepage;