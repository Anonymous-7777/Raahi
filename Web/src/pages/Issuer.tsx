import { useState, useRef } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/hooks/use-toast";
import { User, FileText, Heart, Shield, Upload } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { auth, db, firebaseConfig } from "@/lib/firebase";
import { getAuth, createUserWithEmailAndPassword } from "firebase/auth";
import { initializeApp, getApp, deleteApp } from 'firebase/app';
import { addDoc, collection, doc, serverTimestamp, setDoc } from "firebase/firestore";
import { getStorage, ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { ethers } from "ethers";

const Issuer = () => {
  const { toast } = useToast();
  const [open, setOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [txHash, setTxHash] = useState<string | null>(null);
  const [createdTouristId, setCreatedTouristId] = useState<string | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [formData, setFormData] = useState({
    name: "",
    id: "",
    age: "",
    gender: "",
    email: "",
    nationality: "",
    state: "",
    phone: "",
    emergencyContactName: "",
    emergencyContactNumber: "",
    password: "",
    passportNumber: "",
    visaNumber: "",
    visaTimeline: "",
    bloodGroup: "",
    allergies: "",
    medicalRecord: "",
    insuranceAgencyName: "",
    insuranceId: "",
  });



  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setSelectedFile(e.target.files[0]);
    }
  };
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    const requiredFields = ["name", "id", "age", "gender", "email", "nationality", "phone", "emergencyContactName", "emergencyContactNumber", "password"];
    const missing = requiredFields.filter((f) => !(formData as any)[f]);
    if (missing.length) {
      toast({ title: "Incomplete Form", description: `Please fill in: ${missing.join(", ")}`, variant: "destructive" });
      return;
    }

    const secondaryAppName = 'secondary-auth-app';
    let secondaryApp;
    try {
      // 1) Create Firebase Auth account for tourist using a secondary app
      try {
        secondaryApp = initializeApp(firebaseConfig, secondaryAppName);
      } catch (error) {
        // If the app is already initialized, get the existing app
        secondaryApp = getApp(secondaryAppName);
      }
      const secondaryAuth = getAuth(secondaryApp);
      const touristCred = await createUserWithEmailAndPassword(secondaryAuth, formData.email, formData.password);
      const touristUid = touristCred.user.uid;

      // 2) Write 'users/{uid}' with role=tourist
      await setDoc(doc(db, "users", touristUid), {
        role: "tourist",
        email: formData.email,
        createdAt: serverTimestamp(),
      });

      // 3) Ask MetaMask for Polygon Amoy transaction to get a hash (0-value tx is fine)
      let hash: string | null = null;
      // @ts-ignore
      if (window.ethereum) {
        // @ts-ignore
        const provider = new ethers.BrowserProvider(window.ethereum);
        const signer = await provider.getSigner();
        const network = await provider.getNetwork();
        // Polygon Amoy chainId is 0x13882 (80002)
        const amoyChainId = 80002n;
        if (network.chainId !== amoyChainId) {
          // Try to switch network
          await (window as any).ethereum.request({
            method: "wallet_switchEthereumChain",
            params: [{ chainId: "0x13882" }],
          }).catch(async () => {
            // Try to add the network if not present
            await (window as any).ethereum.request({
              method: "wallet_addEthereumChain",
              params: [{
                chainId: "0x13882",
                chainName: "Polygon Amoy",
                nativeCurrency: { name: "MATIC", symbol: "MATIC", decimals: 18 },
                rpcUrls: ["https://rpc-amoy.polygon.technology"],
                blockExplorerUrls: ["https://www.oklink.com/amoy"],
              }],
            });
          });
        }
        const tx = await signer.sendTransaction({ to: await signer.getAddress(), value: 0n, gasLimit: 30000 });
        const receipt = await tx.wait();
        hash = receipt?.hash ?? tx.hash;
      }




      let photoURL = "";
      if (selectedFile) {
        const storage = getStorage();
        const storageRef = ref(storage, `tourist-photos/${touristUid}`);
        await uploadBytes(storageRef, selectedFile);
        photoURL = await getDownloadURL(storageRef);
      }
      const touristDocRef = doc(db, "tourist", touristUid);
      await setDoc(touristDocRef, {
        uid: touristUid,
        role: "tourist",
        name: formData.name,
        idNumber: formData.id,
        age: formData.age,
        gender: formData.gender,
        email: formData.email,
        nationality: formData.nationality,
        state: formData.state,
        phone: formData.phone,
        emergencyContactName: formData.emergencyContactName,
        emergencyContactNumber: formData.emergencyContactNumber,
        passportNumber: formData.passportNumber,
        visaNumber: formData.visaNumber,
        visaTimeline: formData.visaTimeline,
        bloodGroup: formData.bloodGroup,
        allergies: formData.allergies,
        medicalRecord: formData.medicalRecord,
        insuranceAgencyName: formData.insuranceAgencyName,
        insuranceId: formData.insuranceId,
        blockchainTxHash: hash,
        photoURL: photoURL,
        createdAt: serverTimestamp(),


      });

      setTxHash(hash);
      setCreatedTouristId(touristUid);
      setOpen(true);
      setFormData({
        name: "",
        id: "",
        age: "",
        gender: "",
        email: "",
        nationality: "",
        state: "",
        phone: "",
        emergencyContactName: "",
        emergencyContactNumber: "",
        password: "",
        passportNumber: "",
        visaNumber: "",
        visaTimeline: "",
        bloodGroup: "",
        allergies: "",
        medicalRecord: "",
        insuranceAgencyName: "",
        insuranceId: "",
      });
      setSelectedFile(null);
    } catch (err: any) {
      if (err.code === 'auth/email-already-in-use') {
        toast({ title: "Error", description: "This email is already registered.", variant: "destructive" });
      } else {
        toast({ title: "Error", description: err.message ?? "Failed to register tourist", variant: "destructive" });
      }
    } finally {
      if (secondaryApp) {
        deleteApp(secondaryApp);
      }
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="container mx-auto max-w-4xl">
        <div className="mb-8 text-center">
          <h1 className="text-3xl md:text-4xl font-bold text-foreground mb-2 bg-gradient-to-r from-golden to-green bg-clip-text text-transparent">Traveler Registration</h1>
          <p className="text-muted-foreground">Please fill in all required information for safe travel in North East India</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-8">
          <Card className="border-golden/30">
            <CardHeader className="bg-gradient-to-r from-golden/10 to-green/10 rounded-t-lg">
              <CardTitle className="flex items-center text-foreground">
                <User className="w-5 h-5 mr-2 text-golden" />
                Personal Information
              </CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Full Name *</Label>
                <Input id="name" value={formData.name} onChange={(e) => handleInputChange("name", e.target.value)} placeholder="Enter your full name" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="id">ID Number *</Label>
                <Input id="id" value={formData.id} onChange={(e) => handleInputChange("id", e.target.value)} placeholder="Aadhar/Passport/Other ID" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">Password *</Label>
                <Input id="password" type="password" value={formData.password} onChange={(e) => handleInputChange("password", e.target.value)} placeholder="Set a password for tourist login" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="age">Age *</Label>
                <Input id="age" type="number" value={formData.age} onChange={(e) => handleInputChange("age", e.target.value)} placeholder="Enter age" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="gender">Gender *</Label>
                <Select onValueChange={(value) => handleInputChange("gender", value)}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select gender" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="male">Male</SelectItem>
                    <SelectItem value="female">Female</SelectItem>
                    <SelectItem value="other">Other</SelectItem>
                    <SelectItem value="prefer-not-to-say">Prefer not to say</SelectItem>
                  </SelectContent>
                </Select>
              </div>



              <div className="space-y-2">
                <Label htmlFor="email">Email ID *</Label>
                <Input id="email" type="email" value={formData.email} onChange={(e) => handleInputChange("email", e.target.value)} placeholder="Enter email" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="nationality">Nationality *</Label>
                <Input id="nationality" value={formData.nationality} onChange={(e) => handleInputChange("nationality", e.target.value)} placeholder="Enter nationality" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="state">State (if traveling within India)</Label>
                <Input id="state" value={formData.state} onChange={(e) => handleInputChange("state", e.target.value)} placeholder="Enter state" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="phone">Phone Number *</Label>
                <Input id="phone" type="tel" value={formData.phone} onChange={(e) => handleInputChange("phone", e.target.value)} placeholder="+91-XXXXXXXXXX" required />
              </div>
            </CardContent>
          </Card>





          <Card className="border-green/30">
            <CardHeader className="bg-gradient-to-r from-green/10 to-golden/10 rounded-t-lg">
              <CardTitle className="flex items-center text-foreground">
                <Heart className="w-5 h-5 mr-2 text-green" />
                Emergency Contact
              </CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="emergencyContactName">Emergency Contact Name *</Label>
                <Input id="emergencyContactName" value={formData.emergencyContactName} onChange={(e) => handleInputChange("emergencyContactName", e.target.value)} placeholder="Enter contact person's name" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="emergencyContactNumber">Emergency Contact Number *</Label>
                <Input id="emergencyContactNumber" type="tel" value={formData.emergencyContactNumber} onChange={(e) => handleInputChange("emergencyContactNumber", e.target.value)} placeholder="+91-XXXXXXXXXX" required />
              </div>
            </CardContent>
          </Card>





          <Card className="border-golden/30">
            <CardHeader className="bg-gradient-to-r from-golden/10 to-green/10 rounded-t-lg">
              <CardTitle className="flex items-center text-foreground">
                <FileText className="w-5 h-5 mr-2 text-golden" />
                Travel Documents
              </CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="passportNumber">Passport Number</Label>
                <Input id="passportNumber" value={formData.passportNumber} onChange={(e) => handleInputChange("passportNumber", e.target.value)} placeholder="Enter passport number" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="visaNumber">Visa Number</Label>
                <Input id="visaNumber" value={formData.visaNumber} onChange={(e) => handleInputChange("visaNumber", e.target.value)} placeholder="Enter visa number" />
              </div>
              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="visaTimeline">Visa Timeline</Label>
                <Input id="visaTimeline" value={formData.visaTimeline} onChange={(e) => handleInputChange("visaTimeline", e.target.value)} placeholder="Valid from - Valid until" />
              </div>
            </CardContent>
          </Card>







          <Card className="border-green/30">
            <CardHeader className="bg-gradient-to-r from-green/10 to-golden/10 rounded-t-lg">
              <CardTitle className="flex items-center text-foreground">
                <Heart className="w-5 h-5 mr-2 text-green" />
                Medical Information
              </CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="bloodGroup">Blood Group</Label>
                <Select onValueChange={(value) => handleInputChange("bloodGroup", value)}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select blood group" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="A+">A+</SelectItem>
                    <SelectItem value="A-">A-</SelectItem>
                    <SelectItem value="B+">B+</SelectItem>
                    <SelectItem value="B-">B-</SelectItem>
                    <SelectItem value="AB+">AB+</SelectItem>
                    <SelectItem value="AB-">AB-</SelectItem>
                    <SelectItem value="O+">O+</SelectItem>
                    <SelectItem value="O-">O-</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="allergies">Allergies</Label>
                <Input id="allergies" value={formData.allergies} onChange={(e) => handleInputChange("allergies", e.target.value)} placeholder="List any allergies" />
              </div>
              <div className="space-y-2 md:col-span-2">
                <Label htmlFor="medicalRecord">Previous Medical Record</Label>
                <Textarea id="medicalRecord" value={formData.medicalRecord} onChange={(e) => handleInputChange("medicalRecord", e.target.value)} placeholder="Brief medical history, ongoing medications, etc." rows={3} />
              </div>
            </CardContent>
          </Card>

          <Card className="border-green/30">
            <CardHeader className="bg-gradient-to-r from-green/10 to-golden/10 rounded-t-lg">
              <CardTitle className="flex items-center text-foreground">
                <Shield className="w-5 h-5 mr-2 text-green" />
                Insurance Information
              </CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="insuranceAgencyName">Insurance Agency Name</Label>
                <Input id="insuranceAgencyName" value={formData.insuranceAgencyName} onChange={(e) => handleInputChange("insuranceAgencyName", e.target.value)} placeholder="Enter insurance provider name" />
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="insuranceId">Insurance ID</Label>
                <Input id="insuranceId" value={formData.insuranceId} onChange={(e) => handleInputChange("insuranceId", e.target.value)} placeholder="Enter insurance policy number" />
              </div>
            </CardContent>
          </Card>






          <Card className="border-golden/30">
            <CardHeader className="bg-gradient-to-r from-golden/10 to-green/10 rounded-t-lg">
              <CardTitle className="flex items-center text-foreground">
                <Upload className="w-5 h-5 mr-2 text-golden" />
                Photo Upload
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="border-2 border-dashed border-golden/40 rounded-lg p-8 text-center bg-gradient-to-br from-golden/5 to-green/5">
                <Upload className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground mb-2">{selectedFile ? selectedFile.name : "Upload your recent photograph"}</p>
                <Input type="file" ref={fileInputRef} onChange={handleFileChange} className="hidden" accept="image/png, image/jpeg, image/gif" />
                <Button type="button" variant="outline" size="sm" className="border-golden text-foreground" onClick={() => fileInputRef.current?.click()}>
                  Choose File
                </Button>
                <p className="text-xs text-muted-foreground mt-2">
                  Supported formats: JPG, PNG, GIF (Max 5MB)
                </p>
              </div>
            </CardContent>
          </Card>

          <Separator />




          <div className="flex justify-center">
            <Button 
              type="submit" 
              size="lg"
              className="bg-gradient-to-r from-golden to-green hover:from-golden-dark hover:to-green-dark text-white px-12"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Submitting..." : "Submit Registration"}
            </Button>
          </div>
        </form>





        <Dialog open={open} onOpenChange={setOpen}>
          <DialogContent className="sm:max-w-md border-golden/40 bg-gradient-to-br from-golden/5 to-green/5">
            <DialogHeader>
              <DialogTitle className="text-foreground">Submitted Successfully</DialogTitle>
              <DialogDescription className="text-muted-foreground">
                Traveler information has been registered.
              </DialogDescription>
            </DialogHeader>
            <div className="py-2 text-sm text-foreground">
              <p>Tourist Profile Link:</p>
              <div className="flex items-center space-x-2 mt-2">
                {createdTouristId && <Input value={`${window.location.origin}/tourist/${createdTouristId}`} readOnly />}
                {createdTouristId && <Button onClick={() => navigator.clipboard.writeText(`${window.location.origin}/tourist/${createdTouristId}`)}>Copy</Button>}
              </div>
              {txHash && (
                <div className="mt-4">
                  <p>Blockchain Transaction Hash:</p>
                  <a href={`https://www.oklink.com/amoy/tx/${txHash}`} target="_blank" rel="noopener noreferrer" className="text-blue-500 hover:underline">{txHash}</a>
                </div>
              )}
            </div>
            <DialogFooter>
              <Button onClick={() => setOpen(false)} className="bg-gradient-to-r from-golden to-green hover:from-golden-dark hover:to-green-dark text-white">Close</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
};
export default Issuer;