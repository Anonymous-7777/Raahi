import { Button } from "@/components/ui/button";
import { useNavigate, useLocation } from "react-router-dom";
import { LogOut } from "lucide-react";
import { useAuth } from "@/lib/auth";

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const isLoggedIn = !!user;

  const handleAuthAction = async () => {
    if (isLoggedIn) {
      await logout();
      navigate("/");
    } else {
      navigate("/login");
    }
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <div 
          className="flex items-center space-x-2 cursor-pointer" 
          onClick={() => navigate("/")}
        >
          {/* Logo + Brand */}
          <img
            src={new URL("../assets/raahi-logo.png", import.meta.url).href}
            alt="Raahi logo"
            className="h-12 w-12 md:h-14 md:w-14 mr-3 select-none"
            draggable={false}
          />
          <h1 className="text-2xl font-bold bg-gradient-to-r from-golden to-green bg-clip-text text-transparent">
            Raahi
          </h1>
        </div>
        
        <div className="flex items-center space-x-2">
          {isLoggedIn && location.pathname === "/authority" && (
            <Button variant="outline" onClick={() => navigate("/authority-profile")}>
              Profiles
            </Button>
          )}
          <Button 
            variant={isLoggedIn ? "outline" : "default"}
            onClick={handleAuthAction}
            className={!isLoggedIn ? "bg-gradient-to-r from-golden to-green hover:from-golden-dark hover:to-green-dark text-white" : ""}
          >
            {isLoggedIn ? (
              <>
                <LogOut className="w-4 h-4 mr-2" />
                Logout
              </>
            ) : (
              "Login"
            )}
          </Button>
        </div>
      </div>
    </header>
  );
};

export default Header;
