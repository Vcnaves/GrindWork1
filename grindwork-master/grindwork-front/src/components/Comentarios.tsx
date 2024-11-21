import { useState, useEffect } from "react";
import { FaStar } from "react-icons/fa";
import { useLocation } from "react-router-dom";
import Api from "../api/Api";
import Swal from "sweetalert2";

interface Comment {
  id: number;
  anuncioId: number;
  usuarioId: any;
  usuarioNome: string;
  nota: number;
  comentario: string;
  dataAvaliacao: string;
}

const Comentarios = () => {
  const [userData, setUserData] = useState({
    anuncio: [],
    data_nascimento: "",
    email: "",
    endereco: "",
    id: null,
    imagemUrl: "",
    localidade: { id: null, cidade: "", estado: "" },
    nome: "",
    senha: "",
    telefone: "",
  });
  const location = useLocation();
  const [comments, setComments] = useState<Comment[]>([]);
  const [displayedComments, setDisplayedComments] = useState(3);
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState<number | null>(null);
  const [commentText, setCommentText] = useState("");
  const [userLoggedIn, setUserLoggedIn] = useState(false);
  const [showThankYouMessage, setShowThankYouMessage] = useState(false);
  const [isOwner, setIsOwner] = useState(false);
  const anuncioId = location.state.data.id;

  const [userId] = useState("");

  useEffect(() => {
    const data = JSON.parse(localStorage.getItem("dadosUsuario") || "[]")[0];
    if (data) {
      setUserData({
        anuncio: data.anuncio || [],
        data_nascimento: data.data_nascimento || "",
        email: data.email || "",
        endereco: data.endereco || "",
        id: data.id || "",
        imagemUrl: data.imagemUrl || "",
        localidade: {
          id: data.localidade?.id || "",
          cidade: data.localidade?.cidade || "",
          estado: data.localidade?.estado || "",
        },
        nome: data.nome || "",
        senha: data.senha || "",
        telefone: data.telefone || "",
      });
    }
  }, []);

  useEffect(() => {
    if (userData) {
      if (userData.id == location.state.data.usuario.id) {
        setIsOwner(true);
      }
    }
  }, [userData]);

  const handleLoadMore = () => {
    setDisplayedComments(displayedComments + 3);
  };

  const handleSubmitComment = async () => {
    if (userData) {
      if (userData.id == location.state.data.usuario.id) {
        Swal.fire({
          title: "Atenção!",
          text: "Você não pode comentar no seu próprio anúncio.",
          icon: "warning",
        });
        return;
      }

      if (rating === 0 || commentText === "") {
        Swal.fire({
          title: "Atenção!",
          text: "Por favor, preencha a nota e o comentário.",
          icon: "warning",
        });
        return;
      }

      const anunciosSalvos = localStorage.getItem("anuncios");
      let updatedComments = [...comments];

      if (anunciosSalvos) {
        const anunciosArray = JSON.parse(anunciosSalvos);
        const anuncioIndex = anunciosArray.findIndex(
          (a: any) => a.id === Number(anuncioId)
        );

        if (anuncioIndex !== -1) {
          const existingCommentIndex = anunciosArray[
            anuncioIndex
          ].avaliacao.findIndex(
            (comment: Comment) => comment.usuarioId === Number(userId)
          );

          if (existingCommentIndex !== -1) {
            anunciosArray[anuncioIndex].avaliacao.splice(
              existingCommentIndex,
              1
            );
            localStorage.setItem("anuncios", JSON.stringify(anunciosArray));
          }
        }
      }

      const newComment: Comment = {
        id: updatedComments.length + 1,
        anuncioId: anuncioId,
        usuarioId: userData.id,
        usuarioNome: userData.nome,
        nota: rating,
        comentario: commentText,
        dataAvaliacao: new Date().toISOString().slice(0, 19).replace("T", " "),
      };
      updatedComments = [newComment, ...updatedComments];

      try {

        Swal.fire({
          title: "Aguarde",
          text: "Estamos salvando seu comentário!",
          didOpen: () => {
            Swal.showLoading();
          },
          allowOutsideClick: false,
        });

        await Api.post(`/anuncio/inserirAvaliacao`, newComment);

        const anunciosSalvos = localStorage.getItem("anuncios");
        if (anunciosSalvos) {
          const anunciosArray = JSON.parse(anunciosSalvos);
          const anuncioIndex = anunciosArray.findIndex(
            (a: any) => a.id === anuncioId
          );

          if (anuncioIndex !== -1) {
            anunciosArray[anuncioIndex].avaliacao.push(newComment);
            localStorage.setItem("anuncios", JSON.stringify(anunciosArray));
          }
        }

        const updatedAnunciosSalvos = localStorage.getItem("anuncios");
        if (updatedAnunciosSalvos) {
          const anunciosArray = JSON.parse(updatedAnunciosSalvos);
          const anuncio = anunciosArray.find((a: any) => a.id === anuncioId);
          if (anuncio && anuncio.avaliacao) {
            setComments(anuncio.avaliacao);
          }
        }
        Swal.close();
        setRating(0);
        setCommentText("");
        setShowThankYouMessage(true);
      } catch (error: any) {
        console.error("Erro ao enviar o comentário:", error);
        Swal.fire({
          title: "Erro ao enviar o comentário!",
          text: `${error.response.data.msg}`,
          icon: "error",
        });
      }
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    setUserLoggedIn(!!token);

    const anunciosSalvos = localStorage.getItem("anuncios");
    if (anunciosSalvos) {
      const anunciosArray = JSON.parse(anunciosSalvos);
      const anuncio = anunciosArray.find((a: any) => a.id === anuncioId);
      if (anuncio && anuncio.avaliacao) {
        const filteredComments = anuncio.avaliacao.filter(
          (comment: Comment) => comment.usuarioId !== userId
        );
        setComments(filteredComments);
      }
    }
  }, [anuncioId, userId]);

  return (
    <div className="p-4 bg-white border rounded-md max-w-md mx-auto">
      <h2 className="text-lg font-bold mb-4">Comentários</h2>

      {userLoggedIn ? (
        !isOwner &&
        (!showThankYouMessage ? (
          <div className="mb-4">
            <h3 className="font-semibold">Deixe sua Avaliação:</h3>
            <div className="flex mb-2">
              {[...Array(5)].map((_, index) => {
                const starRating = index + 1;
                return (
                  <FaStar
                    key={starRating}
                    className="cursor-pointer"
                    size={30}
                    color={
                      starRating <= (hover || rating) ? "#ffc107" : "#e4e5e9"
                    }
                    onClick={() => setRating(starRating)}
                    onMouseEnter={() => setHover(starRating)}
                    onMouseLeave={() => setHover(null)}
                  />
                );
              })}
            </div>
            <textarea
              className="w-full p-2 border rounded-md mb-2 resize-none"
              rows={3}
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              placeholder="Deixe seu comentário"
            ></textarea>
            <button
              className="bg-green-700 text-white px-4 py-2 rounded-md hover:bg-green-900 transition duration-300"
              onClick={handleSubmitComment}
            >
              Enviar Comentário
            </button>
          </div>
        ) : (
          <p className="mb-4 text-green-600">
            Obrigado por avaliar este serviço. <br />A equipe GrindWork agradece
            sua atenção!
          </p>
        ))
      ) : (
        <p className="mb-4 text-gray-600">
          Faça login para deixar um comentário e avaliação.
        </p>
      )}

      <div
        style={{ maxHeight: "300px", overflowY: "auto", marginBottom: "1rem" }}
      >
        {comments.slice(0, displayedComments).map((comment) => (
          <div key={comment.id} className="border-b py-2">
            <div className="flex items-center mb-1">
              <strong className="mr-2">{comment.usuarioNome}</strong>
              <div className="flex">
                {[...Array(5)].map((_, index) => (
                  <FaStar
                    key={index}
                    size={20}
                    color={index + 1 <= comment.nota ? "#ffc107" : "#e4e5e9"}
                  />
                ))}
              </div>
            </div>
            <p className="text-gray-700">{comment.comentario}</p>
            <small className="text-gray-500">{comment.dataAvaliacao}</small>
          </div>
        ))}
      </div>

      {displayedComments < comments.length && (
        <button
          onClick={handleLoadMore}
          className="w-full bg-gray-300 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-400 transition duration-300"
        >
          Carregar Mais Comentários
        </button>
      )}
    </div>
  );
};

export default Comentarios;
