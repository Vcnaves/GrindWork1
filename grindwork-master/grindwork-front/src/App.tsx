import { Route, Routes } from "react-router-dom";
import Main from "./components/Main";
import Details from "./components/Details";
import Anunciar from "./components/Anunciar";
import Perfil from "./components/Perfil";
import MeusAnuncios from "./components/MeusAnuncios";
import TermosCondicoes from "./components/TermosCondicoes";
import Usuarios from "./components/Usuarios";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/details" element={<Details />} />
        <Route path="/cadastrar-anuncio" element={<Anunciar />} />
        <Route path="/meus-anuncios" element={<MeusAnuncios />} />
        <Route path="/perfil" element={<Perfil />} />
        <Route path="/termos" element={<TermosCondicoes />} />
        <Route path="/usuarios" element={<Usuarios />} />
      </Routes>
    </>
  );
}

export default App;
